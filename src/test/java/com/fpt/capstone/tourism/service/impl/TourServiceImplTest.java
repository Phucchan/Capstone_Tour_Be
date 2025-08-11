package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.FeedbackDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDayDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourDetailMapper;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.enums.Region;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.tour.*;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceImplTest {

    @InjectMocks
    private TourServiceImpl tourService;

    @Mock
    private TourRepository tourRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private TourPaxRepository tourPaxRepository;
    @Mock
    private TourScheduleRepository tourScheduleRepository;
    @Mock
    private TourDayRepository tourDayRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TourDiscountRepository tourDiscountRepository;
    @Mock
    private TourDetailMapper tourDetailMapper;
    @Mock
    private TourMapper tourMapper;

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour với khoảng giá priceMin=5,000,000, priceMax=10,000,000")
    void filterTours_whenFilteredByPriceRange_shouldSucceed() {
        // Arrange
        double priceMin = 5_000_000.0;
        double priceMax = 10_000_000.0;
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("Hà Nội").build();
        Tour tour = Tour.builder()
                .id(1L)
                .name("Tour Vịnh Hạ Long 2N1D")
                .thumbnailUrl("halong.jpg")
                .durationDays(2)
                .region(Region.SOUTH)
                .tourTransport(TourTransport.CAR)
                .departLocation(departLocation)
                .code("HL01")
                .build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        // Mock các dependency trong hàm mapTourPageToPagingDTO
        when(feedbackRepository.findAverageRatingByTourId(1L)).thenReturn(4.5);
        when(tourPaxRepository.findStartingPriceByTourId(1L)).thenReturn(7_500_000.0); // Giá nằm trong khoảng
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(priceMin, priceMax, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        TourSummaryDTO dto = result.getItems().get(0);
        assertEquals("Tour Vịnh Hạ Long 2N1D", dto.getName());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(7_500_000.0, dto.getStartingPrice());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour với departId=1, destId=2, date='2025-09-15'")
    void filterTours_whenFilteredByLocationsAndDate_shouldSucceed() {
        // Arrange
        Long departId = 1L;
        Long destId = 2L;
        LocalDate date = LocalDate.of(2025, 9, 15);
        Pageable pageable = PageRequest.of(0, 5);

        Location departLocation = Location.builder().name("Hà Nội").build();
        Tour tour = Tour.builder()
                .id(2L)
                .name("Tour Hà Nội - Sapa")
                .thumbnailUrl("sapa.jpg")
                .durationDays(3)
                .region(Region.SOUTH)
                .tourTransport(TourTransport.TRAIN)
                .departLocation(departLocation)
                .code("SP01")
                .build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(feedbackRepository.findAverageRatingByTourId(2L)).thenReturn(4.9);
        when(tourPaxRepository.findStartingPriceByTourId(2L)).thenReturn(3_200_000.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(TourSchedule.builder().departureDate(date.atStartOfDay()).build()));

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, departId, destId, date, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Hà Nội - Sapa", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Abnormal Case: Không tìm thấy tour nào, trả về danh sách rỗng")
    void filterTours_whenNoToursFound_shouldReturnEmpty() {
        // Arrange
        double priceMin = 99_000_000.0; // Một mức giá rất cao để đảm bảo không có kết quả
        Pageable pageable = PageRequest.of(0, 10);
        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(priceMin, null, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_FAIL + " - No tours matched the criteria.");
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotal());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour không có tham số, chỉ phân trang")
    void filterTours_withNoParameters_shouldReturnAllPublishedTours() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 5); // Trang thứ 2, 5 item/trang

        Location location1 = Location.builder().name("Hà Nội").build();
        Location location2 = Location.builder().name("TP. Hồ Chí Minh").build();

        Tour tour1 = Tour.builder().id(1L).name("Tour 1").departLocation(location1).build();
        Tour tour2 = Tour.builder().id(2L).name("Tour 2").departLocation(location2).build();

        Page<Tour> tourPage = new PageImpl<>(List.of(tour1, tour2), pageable, 2);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        // Mock dependencies for tour 1
        when(feedbackRepository.findAverageRatingByTourId(1L)).thenReturn(4.0);
        when(tourPaxRepository.findStartingPriceByTourId(1L)).thenReturn(1000.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        // Mock dependencies for tour 2
        when(feedbackRepository.findAverageRatingByTourId(2L)).thenReturn(4.5);
        when(tourPaxRepository.findStartingPriceByTourId(2L)).thenReturn(2000.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(1, result.getPage()); // Kiểm tra đúng trang trả về
        assertEquals(5, result.getSize()); // Kiểm tra đúng size trả về
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour với giá tối thiểu và điểm đến (priceMin=2,000,000, destId=3)")
    void filterTours_whenFilteredByMinPriceAndDestination_shouldSucceed() {
        // Arrange
        double priceMin = 2_000_000.0;
        Long destId = 3L;
        Pageable pageable = PageRequest.of(0, 8);

        Location departLocation = Location.builder().name("Đà Nẵng").build();
        Tour tour = Tour.builder()
                .id(5L)
                .name("Tour Đà Nẵng - Hội An")
                .thumbnailUrl("hoian.jpg")
                .durationDays(1)
                .region(Region.CENTRAL)
                .tourTransport(TourTransport.CAR)
                .departLocation(departLocation)
                .code("DA01")
                .build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        // Mock dependencies
        when(feedbackRepository.findAverageRatingByTourId(5L)).thenReturn(4.8);
        when(tourPaxRepository.findStartingPriceByTourId(5L)).thenReturn(2_500_000.0); // Giá > priceMin
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(priceMin, null, null, destId, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Đà Nẵng - Hội An", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour với tất cả tham số")
    void filterTours_whenFilteredByAllParameters_shouldSucceed() {
        // Arrange
        double priceMin = 1_000_000.0;
        double priceMax = 5_000_000.0;
        Long departId = 1L;
        Long destId = 2L;
        LocalDate date = LocalDate.of(2025, 10, 10);
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("Hà Nội").build();
        Tour tour = Tour.builder()
                .id(6L)
                .name("Tour Toàn Diện")
                .thumbnailUrl("ful-tour.jpg")
                .durationDays(4)
                .region(Region.SOUTH)
                .tourTransport(TourTransport.PLANE)
                .departLocation(departLocation)
                .code("TD01")
                .build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        // Mock dependencies
        when(feedbackRepository.findAverageRatingByTourId(6L)).thenReturn(4.6);
        when(tourPaxRepository.findStartingPriceByTourId(6L)).thenReturn(4_000_000.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(TourSchedule.builder().departureDate(date.atStartOfDay()).build()));

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(priceMin, priceMax, departId, destId, date, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Toàn Diện", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Abnormal Case: Khoảng giá không hợp lệ (min > max), nên trả về rỗng")
    void filterTours_whenInvalidPriceRange_shouldReturnEmpty() {
        // Arrange
        double priceMin = 10_000_000.0;
        double priceMax = 5_000_000.0; // min > max
        Pageable pageable = PageRequest.of(0, 10);

        // Giả sử repository sẽ không tìm thấy gì với spec này
        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(priceMin, priceMax, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_FAIL + " - Invalid price range.");
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotal());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour chỉ với giá tối thiểu (priceMin=3,000,000)")
    void filterTours_whenFilteredByMinPriceOnly_shouldSucceed() {
        // Arrange
        double priceMin = 3_000_000.0;
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("TP. Hồ Chí Minh").build();
        Tour tour = Tour.builder()
                .id(7L)
                .name("Tour Miền Tây Sông Nước")
                .departLocation(departLocation)
                .build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(tourPaxRepository.findStartingPriceByTourId(7L)).thenReturn(3_500_000.0); // Giá > priceMin
        when(feedbackRepository.findAverageRatingByTourId(7L)).thenReturn(4.2);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(priceMin, null, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Miền Tây Sông Nước", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour chỉ với giá tối đa (priceMax=8,000,000)")
    void filterTours_whenFilteredByMaxPriceOnly_shouldSucceed() {
        // Arrange
        double priceMax = 8_000_000.0;
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("Hà Nội").build();
        Tour tour = Tour.builder()
                .id(8L)
                .name("Tour Ninh Bình Tràng An")
                .departLocation(departLocation)
                .build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(tourPaxRepository.findStartingPriceByTourId(8L)).thenReturn(7_900_000.0); // Giá < priceMax
        when(feedbackRepository.findAverageRatingByTourId(8L)).thenReturn(4.9);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, priceMax, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Ninh Bình Tràng An", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour chỉ với ngày khởi hành (date='2026-01-01')")
    void filterTours_whenFilteredByDateOnly_shouldSucceed() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 1, 1);
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("Quy Nhơn").build();
        Tour tour = Tour.builder().id(9L).name("Tour Eo Gió Kỳ Co").departLocation(departLocation).build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(tourPaxRepository.findStartingPriceByTourId(9L)).thenReturn(1_800_000.0);
        when(feedbackRepository.findAverageRatingByTourId(9L)).thenReturn(4.7);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(TourSchedule.builder().departureDate(date.atStartOfDay()).build()));

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, null, null, date, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Eo Gió Kỳ Co", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour trả về nhiều kết quả (destId=50)")
    void filterTours_whenMultipleResultsFound_shouldReturnAll() {
        // Arrange
        Long destId = 50L;
        Pageable pageable = PageRequest.of(0, 10);

        Location location = Location.builder().name("Phú Quốc").build();
        Tour tour1 = Tour.builder().id(10L).name("Tour 4 đảo Phú Quốc").departLocation(location).build();
        Tour tour2 = Tour.builder().id(11L).name("Tour Grand World Phú Quốc").departLocation(location).build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour1, tour2), pageable, 2);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        // Mock dependencies for tour 10
        when(tourPaxRepository.findStartingPriceByTourId(10L)).thenReturn(1_200_000.0);
        when(feedbackRepository.findAverageRatingByTourId(10L)).thenReturn(4.8);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(10L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        // Mock dependencies for tour 11
        when(tourPaxRepository.findStartingPriceByTourId(11L)).thenReturn(800_000.0);
        when(feedbackRepository.findAverageRatingByTourId(11L)).thenReturn(4.5);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(11L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, null, destId, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals("Tour 4 đảo Phú Quốc", result.getItems().get(0).getName());
        assertEquals("Tour Grand World Phú Quốc", result.getItems().get(1).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Kiểm tra phân trang (page=2, size=1)")
    void filterTours_whenPaging_shouldReturnCorrectPage() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 1); // Yêu cầu trang 3, mỗi trang 1 item

        Location location = Location.builder().name("Hà Giang").build();
        Tour tour = Tour.builder().id(12L).name("Tour Lũng Cú").departLocation(location).build();
        // Giả sử có tổng 5 item, khi yêu cầu trang 2, size 1, repo trả về 1 item
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 5);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(tourPaxRepository.findStartingPriceByTourId(12L)).thenReturn(5_000_000.0);
        when(feedbackRepository.findAverageRatingByTourId(12L)).thenReturn(5.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, null, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getTotal());
        assertEquals(2, result.getPage()); // Kiểm tra đúng trang trả về
        assertEquals(1, result.getSize()); // Kiểm tra đúng size trả về
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Abnormal Case: Lọc tour với ngày trong quá khứ (date='2020-01-01')")
    void filterTours_whenDateIsInThePast_shouldReturnEmpty() {
        // Arrange
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        Pageable pageable = PageRequest.of(0, 10);

        // Specification cho ngày trong quá khứ sẽ không trả về kết quả nào
        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, null, null, pastDate, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_FAIL + " - Date is in the past.");
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotal());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour chỉ với điểm đi (departId=10)")
    void filterTours_whenFilteredByDepartureOnly_shouldSucceed() {
        // Arrange
        Long departId = 10L;
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("Cần Thơ").build();
        Tour tour = Tour.builder().id(13L).name("Tour Chợ Nổi Cái Răng").departLocation(departLocation).build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(tourPaxRepository.findStartingPriceByTourId(13L)).thenReturn(950_000.0);
        when(feedbackRepository.findAverageRatingByTourId(13L)).thenReturn(4.6);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, departId, null, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Chợ Nổi Cái Răng", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("[filterTours] Normal Case: Lọc tour chỉ với điểm đến (destId=20)")
    void filterTours_whenFilteredByDestinationOnly_shouldSucceed() {
        // Arrange
        Long destId = 20L;
        Pageable pageable = PageRequest.of(0, 10);

        Location departLocation = Location.builder().name("Sài Gòn").build();
        Tour tour = Tour.builder().id(14L).name("Tour Vũng Tàu").departLocation(departLocation).build();
        Page<Tour> tourPage = new PageImpl<>(List.of(tour), pageable, 1);

        when(tourRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tourPage);
        when(tourPaxRepository.findStartingPriceByTourId(14L)).thenReturn(1_500_000.0);
        when(feedbackRepository.findAverageRatingByTourId(14L)).thenReturn(4.3);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        PagingDTO<TourSummaryDTO> result = tourService.filterTours(null, null, null, destId, null, pageable);

        // Assert
        System.out.println("Test Log: " + Constants.Message.SEARCH_SUCCESS);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Tour Vũng Tàu", result.getItems().get(0).getName());
        verify(tourRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
    @Test
    @DisplayName("[getTourDetailById] Normal Case: Tour tồn tại với đầy đủ dữ liệu, trả về TourDetailDTO hoàn chỉnh")
    void getTourDetailById_whenTourExistsWithAllData_shouldReturnFullDetailDTO() {
        // Arrange
        Long tourId = 1L;
        Tour mockTour = Tour.builder().id(tourId).name("Test Tour").build();
        TourDay mockTourDay = TourDay.builder().id(10L).dayNumber(1).build();
        Feedback mockFeedback = Feedback.builder().id(20L).rating(5).comment("Great!").build();
        TourPax mockTourPax = TourPax.builder().id(40L).maxQuantity(25).sellingPrice(1000.0).build();
        TourSchedule mockSchedule = TourSchedule.builder().id(30L).tour(mockTour).tourPax(mockTourPax).build();

        // Giả lập các DTO mà mapper sẽ trả về
        TourDetailDTO mockTourDetailDTO = TourDetailDTO.builder().id(tourId).name("Test Tour").build();
        TourDayDetailDTO mockTourDayDTO = TourDayDetailDTO.builder().id(10L).build();
        FeedbackDTO mockFeedbackDTO = FeedbackDTO.builder().id(20L).build();
        TourScheduleDTO mockScheduleDTO = TourScheduleDTO.builder().id(30L).build();

        // Giả lập các lời gọi đến repository
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mockTour));
        when(tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId)).thenReturn(List.of(mockTourDay));
        when(feedbackRepository.findByBooking_TourSchedule_Tour_Id(tourId)).thenReturn(List.of(mockFeedback));
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(tourId), any(LocalDateTime.class))).thenReturn(List.of(mockSchedule));
        when(feedbackRepository.findAverageRatingByTourId(tourId)).thenReturn(4.8);
        when(bookingRepository.sumGuestsByTourScheduleId(30L)).thenReturn(10); // Giả sử đã có 10 người đặt

        // Giả lập các lời gọi đến mapper
        when(tourDetailMapper.tourToTourDetailDTO(mockTour)).thenReturn(mockTourDetailDTO);
        when(tourDetailMapper.tourDayToTourDayDetailDTO(mockTourDay)).thenReturn(mockTourDayDTO);
        when(tourDetailMapper.feedbackToFeedbackDTO(mockFeedback)).thenReturn(mockFeedbackDTO);
        when(tourDetailMapper.tourScheduleToTourScheduleDTO(mockSchedule)).thenReturn(mockScheduleDTO);

        // Act
        TourDetailDTO result = tourService.getTourDetailById(tourId);

        // Assert
        assertNotNull(result);
        assertEquals(tourId, result.getId());
        assertEquals("Test Tour", result.getName());
        assertEquals(4.8, result.getAverageRating());

        assertNotNull(result.getDays());
        assertEquals(1, result.getDays().size());

        assertNotNull(result.getFeedback());
        assertEquals(1, result.getFeedback().size());

        assertNotNull(result.getSchedules());
        assertEquals(1, result.getSchedules().size());
        assertEquals(15, result.getSchedules().get(0).getAvailableSeats()); // 25 tổng - 10 đã đặt = 15

        // Verify (Xác minh các phương thức đã được gọi đúng)
        verify(tourRepository, times(1)).findById(tourId);
        verify(feedbackRepository, times(1)).findAverageRatingByTourId(tourId);
        verify(bookingRepository, times(1)).sumGuestsByTourScheduleId(30L);
    }

    @Test
    @DisplayName("[getTourDetailById] Abnormal Case: Tour không tồn tại, ném ra BusinessException")
    void getTourDetailById_whenTourNotFound_shouldThrowBusinessException() {
        // Arrange
        Long nonExistentTourId = 99L;
        when(tourRepository.findById(nonExistentTourId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourService.getTourDetailById(nonExistentTourId);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        assertEquals("Tour not found", exception.getMessage());

        // Xác minh rằng các repository khác không được gọi đến
        verify(tourDayRepository, never()).findByTourIdOrderByDayNumberAsc(anyLong());
        verify(feedbackRepository, never()).findAverageRatingByTourId(anyLong());
    }

    @Test
    @DisplayName("[getTourDetailById] Normal Case: Tour tồn tại nhưng không có feedback, trả về DTO với danh sách feedback rỗng")
    void getTourDetailById_whenTourExistsWithNoFeedback_shouldReturnDTOWithEmptyFeedback() {
        // Arrange
        Long tourId = 2L;
        Tour mockTour = Tour.builder().id(tourId).name("New Tour").build();
        TourDetailDTO mockTourDetailDTO = TourDetailDTO.builder().id(tourId).name("New Tour").build();

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mockTour));
        // Không có feedback
        when(feedbackRepository.findByBooking_TourSchedule_Tour_Id(tourId)).thenReturn(Collections.emptyList());
        when(feedbackRepository.findAverageRatingByTourId(tourId)).thenReturn(null); // Repository trả về null khi không có rating
        // Các dữ liệu khác vẫn có thể có hoặc không
        when(tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId)).thenReturn(Collections.emptyList());
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(tourId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(tourDetailMapper.tourToTourDetailDTO(mockTour)).thenReturn(mockTourDetailDTO);

        // Act
        TourDetailDTO result = tourService.getTourDetailById(tourId);

        // Assert
        assertNotNull(result);
        assertEquals(tourId, result.getId());
        assertNull(result.getAverageRating()); // Điểm đánh giá trung bình phải là null
        assertTrue(result.getFeedback().isEmpty()); // Danh sách feedback phải rỗng
    }

    @Test
    @DisplayName("[getTourDetailById] Normal Case: Tour tồn tại nhưng không có lịch trình trong tương lai, trả về DTO với danh sách schedule rỗng")
    void getTourDetailById_whenTourExistsWithNoFutureSchedules_shouldReturnDTOWithEmptySchedules() {
        // Arrange
        Long tourId = 3L;
        Tour mockTour = Tour.builder().id(tourId).name("Old Tour").build();
        TourDetailDTO mockTourDetailDTO = TourDetailDTO.builder().id(tourId).name("Old Tour").build();

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mockTour));
        // Không có lịch trình trong tương lai
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(tourId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        // Các dữ liệu khác vẫn tồn tại
        when(feedbackRepository.findByBooking_TourSchedule_Tour_Id(tourId)).thenReturn(Collections.emptyList());
        when(feedbackRepository.findAverageRatingByTourId(tourId)).thenReturn(4.0);
        when(tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId)).thenReturn(Collections.emptyList());
        when(tourDetailMapper.tourToTourDetailDTO(mockTour)).thenReturn(mockTourDetailDTO);

        // Act
        TourDetailDTO result = tourService.getTourDetailById(tourId);

        // Assert
        assertNotNull(result);
        assertEquals(tourId, result.getId());
        assertEquals(4.0, result.getAverageRating());
        assertTrue(result.getSchedules().isEmpty()); // Danh sách lịch trình phải rỗng
    }
}
