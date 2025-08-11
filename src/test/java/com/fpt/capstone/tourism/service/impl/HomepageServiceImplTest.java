package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.dto.response.homepage.PopularLocationDTO;
import com.fpt.capstone.tourism.dto.response.homepage.SaleTourDTO;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.enums.Region;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDiscount;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.repository.tour.TourDiscountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomepageServiceImplTest {

    @InjectMocks
    private HomepageServiceImpl homepageService;

    // Mocks for all dependencies
    @Mock private FeedbackRepository feedbackRepository;
    @Mock private BlogRepository blogRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private TourDiscountRepository tourDiscountRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private BlogMapper blogMapper;
    @Mock private LocationMapper locationMapper;

    // --- Constants for hardcoded numbers in the service ---
    private static final int NUM_LOCATIONS_REQUIRED = 8;
    private static final Pageable SALE_TOUR_PAGEABLE = PageRequest.of(0, 5);

    @Test
    @DisplayName("[getHomepageData] Normal Case: Trả về đầy đủ dữ liệu khi tất cả repository đều có kết quả")
    void getHomepageData_whenAllDataExists_shouldReturnFullData() {
        // Arrange
        // 1. Mock data for Locations (giả sử tìm thấy đủ 8 địa điểm nổi bật)
        List<Location> popularLocations = new ArrayList<>();
        for (int i = 1; i <= NUM_LOCATIONS_REQUIRED; i++) {
            popularLocations.add(Location.builder().id((long) i).name("Location " + i).build());
        }
        when(locationRepository.findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED))).thenReturn(popularLocations);
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenAnswer(inv -> new PopularLocationDTO(inv.getArgument(0, Location.class).getId(), null, null));

        // 2. Mock data for Recent Blogs (giả sử tìm thấy 1 blog)
        Blog blog1 = Blog.builder().id(101L).title("Blog 1").blogTags(Collections.emptyList()).build();
        List<Blog> recentBlogs = List.of(blog1);
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(recentBlogs);
        when(blogMapper.blogToBlogSummaryDTO(any(Blog.class))).thenReturn(new BlogSummaryDTO());

        // 3. Mock data for Sale Tours (giả sử tìm thấy 1 tour giảm giá)
        Location departLocation = Location.builder().id(1L).name("Hà Nội").build();
        Tour tour = Tour.builder().id(201L).name("Tour Sale").region(Region.SOUTH).departLocation(departLocation).build();
        TourPax tourPax = TourPax.builder().sellingPrice(1000.0).maxQuantity(20).build();
        TourSchedule schedule = TourSchedule.builder().id(301L).tour(tour).tourPax(tourPax).departureDate(LocalDateTime.now().plusDays(1)).build();
        TourDiscount discount = TourDiscount.builder().tourSchedule(schedule).discountPercent(10.0f).build();
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE))).thenReturn(List.of(discount));
        when(bookingRepository.sumGuestsByTourScheduleId(anyLong())).thenReturn(5); // Giả sử đã có 5 người đặt

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        assertNotNull(result);
        assertEquals(NUM_LOCATIONS_REQUIRED, result.getLocations().size());
        assertEquals(1, result.getRecentBlogs().size());
        assertEquals(1, result.getSaleTours().size());
        assertEquals(15, result.getSaleTours().get(0).getAvailableSeats()); // 20 - 5 = 15

        // Verify
        verify(locationRepository, times(1)).findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED));
        verify(locationRepository, never()).findRandomLocation(anyInt()); // Không cần gọi vì đã đủ 8
        verify(blogRepository, times(1)).findFirst5ByDeletedFalseOrderByCreatedAtDesc();
        verify(tourDiscountRepository, times(1)).findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE));
    }

    @Test
    @DisplayName("[getHomepageData] Normal Case: Có ít hơn 8 địa điểm nổi bật, cần lấy thêm địa điểm ngẫu nhiên")
    void getHomepageData_whenLessThanEightPopularLocations_shouldFetchRandomLocations() {
        // Arrange
        // 1. Mock popular locations (chỉ tìm thấy 5)
        int numFound = 5;
        int numNeeded = NUM_LOCATIONS_REQUIRED - numFound; // Cần thêm 3
        List<Location> popularLocations = new ArrayList<>();
        for (int i = 1; i <= numFound; i++) {
            popularLocations.add(Location.builder().id((long) i).name("Popular " + i).build());
        }
        when(locationRepository.findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED))).thenReturn(popularLocations);

        // 2. Mock random locations to fill the gap (tìm thấy 3)
        List<Location> randomLocations = new ArrayList<>();
        for (int i = 1; i <= numNeeded; i++) {
            randomLocations.add(Location.builder().id((long) (i + 100)).name("Random " + i).build());
        }
        when(locationRepository.findRandomLocation(eq(numNeeded))).thenReturn(randomLocations);
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenAnswer(inv -> new PopularLocationDTO(inv.getArgument(0, Location.class).getId(), null, null));

        // Mock other data to be empty for simplicity
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE))).thenReturn(Collections.emptyList());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        assertNotNull(result);
        assertEquals(NUM_LOCATIONS_REQUIRED, result.getLocations().size()); // Tổng cộng phải là 8

        // Verify
        verify(locationRepository, times(1)).findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED));
        verify(locationRepository, times(1)).findRandomLocation(eq(numNeeded)); // Xác minh đã gọi để lấy 3 địa điểm còn lại
    }

    @Test
    @DisplayName("[getHomepageData] Abnormal Case: Không có tour giảm giá")
    void getHomepageData_whenNoSaleTours_shouldReturnEmptySaleToursList() {
        // Arrange
        // Mock locations and blogs to have data
        when(locationRepository.findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED))).thenReturn(List.of(Location.builder().id(1L).build()));
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenReturn(new PopularLocationDTO());
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(List.of(Blog.builder().id(101L).blogTags(Collections.emptyList()).build()));
        when(blogMapper.blogToBlogSummaryDTO(any(Blog.class))).thenReturn(new BlogSummaryDTO());

        // Mock sale tours to be empty
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE))).thenReturn(Collections.emptyList());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        assertNotNull(result);
        assertFalse(result.getLocations().isEmpty());
        assertFalse(result.getRecentBlogs().isEmpty());
        assertTrue(result.getSaleTours().isEmpty()); // Danh sách tour giảm giá phải rỗng

        // Verify
        verify(tourDiscountRepository, times(1)).findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE));
    }

    @Test
    @DisplayName("[getHomepageData] Abnormal Case: Không có bài blog nào")
    void getHomepageData_whenNoRecentBlogs_shouldReturnEmptyBlogsList() {
        // Arrange
        // Mock locations and sale tours to have data
        when(locationRepository.findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED))).thenReturn(List.of(Location.builder().id(1L).build()));
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenReturn(new PopularLocationDTO());
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE))).thenReturn(Collections.emptyList());

        // Mock recent blogs to be empty
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        assertNotNull(result);
        assertFalse(result.getLocations().isEmpty());
        assertTrue(result.getRecentBlogs().isEmpty()); // Danh sách blog phải rỗng

        // Verify
        verify(blogRepository, times(1)).findFirst5ByDeletedFalseOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("[getHomepageData] Abnormal Case: Tất cả dữ liệu đều rỗng")
    void getHomepageData_whenAllDataIsEmpty_shouldReturnEmptyHomepageData() {
        // Arrange
        when(locationRepository.findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED))).thenReturn(Collections.emptyList());
        // Service sẽ gọi findRandomLocation với tham số là 8
        when(locationRepository.findRandomLocation(eq(NUM_LOCATIONS_REQUIRED))).thenReturn(Collections.emptyList());
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE))).thenReturn(Collections.emptyList());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        assertNotNull(result);
        assertTrue(result.getLocations().isEmpty());
        assertTrue(result.getRecentBlogs().isEmpty());
        assertTrue(result.getSaleTours().isEmpty());

        // Verify all repositories were called with correct hardcoded numbers
        verify(locationRepository, times(1)).findLocationsWithMostTours(eq(NUM_LOCATIONS_REQUIRED));
        verify(locationRepository, times(1)).findRandomLocation(eq(NUM_LOCATIONS_REQUIRED));
        verify(blogRepository, times(1)).findFirst5ByDeletedFalseOrderByCreatedAtDesc();
        verify(tourDiscountRepository, times(1)).findTopDiscountedTours(any(LocalDateTime.class), eq(SALE_TOUR_PAGEABLE));
    }
}