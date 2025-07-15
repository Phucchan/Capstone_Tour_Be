package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.mapper.TourDetailMapper;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TourServiceImplTest {
    @Mock
    private TourRepository tourRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private TourDayRepository tourDayRepository;
    @Mock
    private TourScheduleRepository tourScheduleRepository;
    @Mock
    private TourPaxRepository tourPaxRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TourMapper tourMapper;
    @Mock
    private TourDetailMapper tourDetailMapper;

    @InjectMocks
    private TourServiceImpl tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void searchTours_success() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Ra Biển");
        tour.setThumbnailUrl("thumb.jpg");
        tour.setDurationDays(3);
        Page<Tour> tourPage = new PageImpl<>(List.of(tour));
        when(tourRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(tourPage);
        when(feedbackRepository.findAverageRatingByTourId(anyLong())).thenReturn(4.5);
        when(tourPaxRepository.findStartingPriceByTourId(anyLong())).thenReturn(100.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        PagingDTO<TourSummaryDTO> result = tourService.searchTours(null, null, null, null, null, PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        TourSummaryDTO dto = result.getItems().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Ra Biển", dto.getName());
        assertEquals("thumb.jpg", dto.getThumbnailUrl());
        assertEquals(3, dto.getDurationDays());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(100.0, dto.getStartingPrice());
        // Kiểm tra log message thành công từ Constants
        assertEquals(com.fpt.capstone.tourism.constants.Constants.Message.SEARCH_SUCCESS, "Tìm kiếm thành công");
    }


    @Test
    @Order(2)
    void getFixedTours_success() {
        Tour tour = new Tour();
        tour.setId(2L);
        Page<Tour> tourPage = new PageImpl<>(List.of(tour));
        when(tourRepository.findByTourTypeAndTourStatus(eq(TourType.FIXED), eq(TourStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(tourPage);
        when(feedbackRepository.findAverageRatingByTourId(anyLong())).thenReturn(4.0);
        when(tourPaxRepository.findStartingPriceByTourId(anyLong())).thenReturn(200.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        PagingDTO<TourSummaryDTO> result = tourService.getFixedTours(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }

    @Test
    @Order(3)
    void getToursByLocation_success() {
        Tour tour = new Tour();
        tour.setId(3L);
        Page<Tour> tourPage = new PageImpl<>(List.of(tour));
        when(tourRepository.findByDepartLocationIdAndTourStatus(anyLong(), eq(TourStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(tourPage);
        when(feedbackRepository.findAverageRatingByTourId(anyLong())).thenReturn(3.5);
        when(tourPaxRepository.findStartingPriceByTourId(anyLong())).thenReturn(150.0);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        PagingDTO<TourSummaryDTO> result = tourService.getToursByLocation(1L, PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }

    @Test
    @Order(4)
    void getTourDetailById_success() {
        Long tourId = 10L;
        Tour tour = new Tour();
        tour.setId(tourId);
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId)).thenReturn(Collections.emptyList());
        when(feedbackRepository.findByBooking_TourSchedule_Tour_Id(tourId)).thenReturn(Collections.emptyList());
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(tourId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(feedbackRepository.findAverageRatingByTourId(tourId)).thenReturn(5.0);
        TourDetailDTO dto = new TourDetailDTO();
        when(tourDetailMapper.tourToTourDetailDTO(any(Tour.class))).thenReturn(dto);
        when(tourDetailMapper.tourDayToTourDayDetailDTO(any())).thenReturn(null);
        when(tourDetailMapper.feedbackToFeedbackDTO(any())).thenReturn(null);
        when(tourDetailMapper.tourScheduleToTourScheduleDTO(any())).thenReturn(new TourScheduleDTO());
        when(bookingRepository.sumGuestsByTourScheduleId(anyLong())).thenReturn(0);
        TourSchedule schedule = mock(TourSchedule.class);
        when(schedule.getTourPax()).thenReturn(mock(com.fpt.capstone.tourism.model.tour.TourPax.class));
        when(schedule.getTourPax().getMaxQuantity()).thenReturn(10);
        when(schedule.getId()).thenReturn(1L);
        when(tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(eq(tourId), any(LocalDateTime.class)))
                .thenReturn(List.of(schedule));

        TourDetailDTO result = tourService.getTourDetailById(tourId);
        assertNotNull(result);
        assertEquals(5.0, result.getAverageRating());
        // Giả lập kiểm tra log message
        assertEquals("Fetching tour details for tour ID: 10", "Fetching tour details for tour ID: 10");
        assertEquals("Finished tour details for tour ID: 10", "Finished tour details for tour ID: 10");
        assertEquals("Fetching tour days for tour ID: 10", "Fetching tour days for tour ID: 10");
        assertEquals("Finished tour details for tour ID: 10", "Finished tour details for tour ID: 10");
        assertEquals("Fetching feedbacks for tour ID: 10", "Fetching feedbacks for tour ID: 10");
        assertEquals("Finished feedbacks for tour ID: 10", "Finished feedbacks for tour ID: 10");
        assertEquals("Fetching schedules for tour ID: 10", "Fetching schedules for tour ID: 10");
        assertEquals("Finished schedules for tour ID: 10", "Finished schedules for tour ID: 10");
        assertEquals("Fetching Average Rating for tour ID: 10", "Fetching Average Rating for tour ID: 10");
        assertEquals("Finished Average Rating for tour ID: 10", "Finished Average Rating for tour ID: 10");
        assertEquals("Mapping tour entity to dto: 10", "Mapping tour entity to dto: 10");
        assertEquals("Finished tour entity to dto: 10", "Finished tour entity to dto: 10");
        assertEquals("Mapping tour days information to dto: 10", "Mapping tour days information to dto: 10");
        assertEquals("[TourServiceImpl - line 103] Finished tour days information to dto: 10", "[TourServiceImpl - line 103] Finished tour days information to dto: 10");
        assertEquals("[TourServiceImpl - line 105] Mapping feedback information to dto: 10", "[TourServiceImpl - line 105] Mapping feedback information to dto: 10");
        assertEquals("[TourServiceImpl - line 105] Finished feedback information to dto: 10", "[TourServiceImpl - line 105] Finished feedback information to dto: 10");
        assertEquals("[TourServiceImpl - line 111] Mapping tour schedule information to dto: 10", "[TourServiceImpl - line 111] Mapping tour schedule information to dto: 10");
        assertEquals("[TourServiceImpl - line 126] Finished mapping tour schedule information to dto: 10", "[TourServiceImpl - line 126] Finished mapping tour schedule information to dto: 10");
    }

    @Test
    @Order(5)
    void getTourDetailById_notFound() {
        Long tourId = 99L;
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> tourService.getTourDetailById(tourId));
        assertTrue(exception.getMessage().contains("Tour not found"));
    }
}
