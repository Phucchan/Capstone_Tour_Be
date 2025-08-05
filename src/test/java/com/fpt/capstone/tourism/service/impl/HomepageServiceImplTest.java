package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.dto.response.homepage.PopularLocationDTO;
import com.fpt.capstone.tourism.dto.response.homepage.SaleTourDTO;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDiscount;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.repository.tour.TourDiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit test cho class HomepageServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class HomepageServiceImplTest {

    @InjectMocks
    private HomepageServiceImpl homepageService;

    // Mocks for repositories and mappers
    @Mock private FeedbackRepository feedbackRepository;
    @Mock private BlogRepository blogRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private TourDiscountRepository tourDiscountRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private BlogMapper blogMapper;
    @Mock private LocationMapper locationMapper;
    @Mock private TourMapper tourMapper;

    // Test data objects (Entities)
    private TourDiscount tourDiscount;
    private TourSchedule tourSchedule;
    private Tour tour;
    private TourPax tourPax;

    @BeforeEach
    void setUp() {
        // Khởi tạo các đối tượng Entity với dữ liệu giả chi tiết và được liên kết với nhau
        // để tránh lỗi NullPointerException khi test
        Location location = Location.builder().id(1L).name("Hà Nội").build();
        tour = Tour.builder().id(1L).name("Tour Đà Nẵng").thumbnailUrl("url").departLocation(location).durationDays(3).code("DN01").build();
        tourPax = TourPax.builder().id(1L).maxQuantity(20).sellingPrice(1000.00).build();
        tourSchedule = TourSchedule.builder().id(1L).tour(tour).tourPax(tourPax).departureDate(LocalDateTime.now().plusDays(10)).build();
        tourDiscount = TourDiscount.builder().id(1L).tourSchedule(tourSchedule).discountPercent(15).build();
    }

    @Test
    @DisplayName("[Normal Case] Tải trang chủ thành công với số lượng dữ liệu mặc định")
    void getHomepageData_DefaultSuccessScenario() {
        // --- Mục đích: Kiểm tra luồng hoạt động thành công nhất, khi tất cả các repository
        // --- đều trả về số lượng dữ liệu mặc định như mong đợi.

        // Arrange: "Dạy" cho các repository giả lập (mock) phải trả về cái gì.

        // 1. Giả lập cho `numberLocation` = 8
        // Tạo ra một danh sách giả chứa 8 địa điểm.
        List<Location> mockLocations = IntStream.range(0, 8)
                .mapToObj(i -> Location.builder().id((long) i).name("Location " + i).build())
                .collect(Collectors.toList());
        when(locationRepository.findLocationsWithMostTours(8)).thenReturn(mockLocations);

        // 2. Giả lập cho `numberSaleTour` = 5
        // Tạo ra một danh sách giả chứa 5 tour giảm giá.
        List<TourDiscount> mockSaleTours = IntStream.range(0, 5)
                .mapToObj(i -> TourDiscount.builder().id((long) i).tourSchedule(tourSchedule).discountPercent(10 + i).build())
                .collect(Collectors.toList());
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(mockSaleTours);

        // 3. Giả lập cho `numberBlog` = 5
        // Tạo ra một danh sách giả chứa 5 bài blog.
        List<Blog> mockBlogs = IntStream.range(0, 5)
                .mapToObj(i -> Blog.builder().id((long) i).title("Blog " + i).build())
                .collect(Collectors.toList());
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(mockBlogs);

        // 4. Giả lập cho `numberBooking` (số ghế đã đặt) để tính toán
        // Giả sử mỗi tour đều có 5 người đặt.
        when(bookingRepository.sumGuestsByTourScheduleId(anyLong())).thenReturn(5);

        // 5. Giả lập các mapper để đảm bảo việc chuyển đổi DTO thành công
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenReturn(new PopularLocationDTO());
        when(blogMapper.blogToBlogSummaryDTO(any(Blog.class))).thenReturn(new BlogSummaryDTO());


        // Act: Gọi phương thức chính cần test
        HomepageDataDTO result = homepageService.getHomepageData();


        // Assert: Kiểm tra xem kết quả trả về có đúng như mong đợi không.
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_SUCCESS);

        assertNotNull(result, "Kết quả không được null");

        // Kiểm tra số lượng
        assertEquals(8, result.getLocations().size(), "Số lượng địa điểm mặc định phải là 8");
        assertEquals(5, result.getSaleTours().size(), "Số lượng tour giảm giá mặc định phải là 5");
        assertEquals(5, result.getRecentBlogs().size(), "Số lượng bài viết gần đây mặc định phải là 5");

        // Kiểm tra logic tính toán `availableSeats`
        // tourPax.getMaxQuantity() là 20, bookedSlots là 5 => availableSeats phải là 15
        SaleTourDTO firstSaleTour = result.getSaleTours().get(0);
        assertEquals(15, firstSaleTour.getAvailableSeats(), "Số ghế trống phải được tính toán chính xác");

        // Verify: Đảm bảo các phương thức repository đã được gọi đúng số lần
        verify(locationRepository, times(1)).findLocationsWithMostTours(8);
        verify(tourDiscountRepository, times(1)).findTopDiscountedTours(any(LocalDateTime.class), eq(PageRequest.of(0, 5)));
        verify(blogRepository, times(1)).findFirst5ByDeletedFalseOrderByCreatedAtDesc();
    }
    @Test
    @DisplayName("[Boundary Case] Không có location nào được tìm thấy")
    void getHomepageData_NoLocations() {
        // Arrange
        when(locationRepository.findLocationsWithMostTours(8)).thenReturn(List.of());
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(tourDiscount));
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(List.of(Blog.builder().id(1L).title("Blog").build()));
        when(bookingRepository.sumGuestsByTourScheduleId(anyLong())).thenReturn(5);
        when(blogMapper.blogToBlogSummaryDTO(any(Blog.class))).thenReturn(new BlogSummaryDTO());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_SUCCESS + " (Boundary: No locations)");
        assertNotNull(result);
        assertTrue(result.getLocations().isEmpty(), "Danh sách địa điểm phải rỗng");
        assertEquals(1, result.getSaleTours().size());
        assertEquals(1, result.getRecentBlogs().size());
    }

    @Test
    @DisplayName("[Boundary Case] Không có sale tour nào được tìm thấy")
    void getHomepageData_NoSaleTours() {
        // Arrange
        when(locationRepository.findLocationsWithMostTours(8)).thenReturn(List.of(Location.builder().id(1L).name("Location").build()));
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of());
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(List.of(Blog.builder().id(1L).title("Blog").build()));
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenReturn(new PopularLocationDTO());
        when(blogMapper.blogToBlogSummaryDTO(any(Blog.class))).thenReturn(new BlogSummaryDTO());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_SUCCESS + " (Boundary: No sale tours)");
        assertNotNull(result);
        assertEquals(1, result.getLocations().size());
        assertTrue(result.getSaleTours().isEmpty(), "Danh sách tour giảm giá phải rỗng");
        assertEquals(1, result.getRecentBlogs().size());
    }

    @Test
    @DisplayName("[Boundary Case] Không có blog nào được tìm thấy")
    void getHomepageData_NoBlogs() {
        // Arrange
        when(locationRepository.findLocationsWithMostTours(8)).thenReturn(List.of(Location.builder().id(1L).name("Location").build()));
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(tourDiscount));
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(List.of());
        when(bookingRepository.sumGuestsByTourScheduleId(anyLong())).thenReturn(5);
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenReturn(new PopularLocationDTO());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_SUCCESS + " (Boundary: No blogs)");
        assertNotNull(result);
        assertEquals(1, result.getLocations().size());
        assertEquals(1, result.getSaleTours().size());
        assertTrue(result.getRecentBlogs().isEmpty(), "Danh sách blog phải rỗng");
    }

    @Test
    @DisplayName("[Negative Case] Không có dữ liệu nào trả về (all empty)")
    void getHomepageData_AllEmpty() {
        // Arrange
        when(locationRepository.findLocationsWithMostTours(8)).thenReturn(List.of());
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of());
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(List.of());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_FAIL + " (Boundary: All empty)");
        assertNotNull(result);
        assertTrue(result.getLocations().isEmpty());
        assertTrue(result.getSaleTours().isEmpty());
        assertTrue(result.getRecentBlogs().isEmpty());
    }

    @Test
    @DisplayName("[Boundary Case] Tour đã full chỗ, availableSeats = 0")
    void getHomepageData_FullBooked() {
        // Arrange
        when(locationRepository.findLocationsWithMostTours(8)).thenReturn(List.of(Location.builder().id(1L).name("Location").build()));
        when(tourDiscountRepository.findTopDiscountedTours(any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(tourDiscount));
        when(blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc()).thenReturn(List.of(Blog.builder().id(1L).title("Blog").build()));
        when(bookingRepository.sumGuestsByTourScheduleId(anyLong())).thenReturn(20); // Full slot
        when(locationMapper.toPopularLocationDTO(any(Location.class))).thenReturn(new PopularLocationDTO());
        when(blogMapper.blogToBlogSummaryDTO(any(Blog.class))).thenReturn(new BlogSummaryDTO());

        // Act
        HomepageDataDTO result = homepageService.getHomepageData();

        // Assert
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_SUCCESS + " (Boundary: Tour fully booked)");
        assertNotNull(result);
        SaleTourDTO saleTourDTO = result.getSaleTours().get(0);
        assertEquals(0, saleTourDTO.getAvailableSeats(), "Available seats phải là 0 khi full booking");
    }

    @Test
    @DisplayName("[Exception Case] Repository bị lỗi, service xử lý exception gracefully")
    void getHomepageData_RepositoryException() {
        // Arrange
        String errorMessage = "Database error";
        when(locationRepository.findLocationsWithMostTours(8)).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        System.out.println("Test Log: " + Constants.Message.HOMEPAGE_LOAD_FAIL + " - Lý do: " + errorMessage);
        Exception exception = assertThrows(RuntimeException.class, () -> homepageService.getHomepageData());

        assertEquals(errorMessage, exception.getMessage());
    }

}