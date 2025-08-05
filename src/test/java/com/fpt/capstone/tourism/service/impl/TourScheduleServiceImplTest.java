package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourScheduleCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleOptionsDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.model.tour.TourTheme;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourScheduleServiceImplTest {

    @InjectMocks
    private TourScheduleServiceImpl tourScheduleService;

    @Mock
    private TourManagementRepository tourRepository;
    @Mock
    private TourPaxRepository tourPaxRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TourScheduleRepository tourScheduleRepository;
    @Mock
    private UserMapper userMapper;

    private Tour tour;
    private TourPax tourPax;
    private User coordinator;
    private TourScheduleCreateRequestDTO createRequestDTO;

    @BeforeEach
    void setUp() {
        tour = Tour.builder()
                .id(1L)
                .name("Amazing Vietnam Tour")
                .tourStatus(TourStatus.PUBLISHED)
                .durationDays(5)
                .themes(new ArrayList<>())
                .build();

        tourPax = TourPax.builder()
                .id(10L)
                .tour(tour)
                .minQuantity(10)
                .maxQuantity(20)
                .sellingPrice(1000.00)
                .extraHotelCost(50.00)
                .build();

        coordinator = User.builder()
                .id(20L)
                .fullName("Coordinator Name")
                .build();

        createRequestDTO = TourScheduleCreateRequestDTO.builder()
                .tourPaxId(10L)
                .coordinatorId(20L)
                .departureDate(LocalDateTime.of(2024, 10, 20, 8, 0))
                .build();
    }

    // region createTourSchedule Tests

    @Test
    void createTourSchedule_Normal_Success() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourPaxRepository.findById(10L)).thenReturn(Optional.of(tourPax));
        when(userRepository.findById(20L)).thenReturn(Optional.of(coordinator));
        when(tourScheduleRepository.save(any(TourSchedule.class))).thenAnswer(invocation -> {
            TourSchedule saved = invocation.getArgument(0);
            saved.setId(100L); // Simulate saving and getting an ID
            return saved;
        });

        // Act
        GeneralResponse<TourScheduleManagerDTO> response = tourScheduleService.createTourSchedule(1L, createRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(Constants.Message.SCHEDULE_CREATED_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(100L, response.getData().getId());

        ArgumentCaptor<TourSchedule> captor = ArgumentCaptor.forClass(TourSchedule.class);
        verify(tourScheduleRepository).save(captor.capture());

        TourSchedule capturedSchedule = captor.getValue();
        assertEquals(tour, capturedSchedule.getTour());
        assertEquals(coordinator, capturedSchedule.getCoordinator());
        assertEquals(tourPax, capturedSchedule.getTourPax());
        assertEquals(createRequestDTO.getDepartureDate(), capturedSchedule.getDepartureDate());
        // End date should be departure + duration - 1
        assertEquals(createRequestDTO.getDepartureDate().plusDays(4), capturedSchedule.getEndDate());
        assertEquals(tourPax.getSellingPrice(), capturedSchedule.getPrice());
        assertEquals(tourPax.getExtraHotelCost(), capturedSchedule.getExtraHotelCost());
        assertEquals(tourPax.getMaxQuantity(), capturedSchedule.getAvailableSeats());
        assertFalse(capturedSchedule.isPublished());
    }

    @Test
    void createTourSchedule_Abnormal_TourNotFound() {
        // Arrange
        when(tourRepository.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.createTourSchedule(1L, createRequestDTO));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        // SỬA LỖI: Sử dụng hằng số từ Constants.java
        assertEquals(Constants.Message.TOUR_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createTourSchedule_Abnormal_TourNotPublished() {
        // Arrange
        tour.setTourStatus(TourStatus.DRAFT);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.createTourSchedule(1L, createRequestDTO));
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
        assertEquals(Constants.Message.TOUR_NOT_PUBLISHED, exception.getMessage());
    }

    @Test
    void createTourSchedule_Abnormal_TourPaxNotFound() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourPaxRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.createTourSchedule(1L, createRequestDTO));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        assertEquals(Constants.Message.TOUR_PAX_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createTourSchedule_Abnormal_TourPaxMismatch() {
        // Arrange
        Tour anotherTour = Tour.builder().id(99L).build();
        tourPax.setTour(anotherTour); // Pax belongs to a different tour
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourPaxRepository.findById(10L)).thenReturn(Optional.of(tourPax));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.createTourSchedule(1L, createRequestDTO));
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
        assertEquals(Constants.Message.TOUR_PAX_MISMATCH, exception.getMessage());
    }

    @Test
    void createTourSchedule_Abnormal_CoordinatorNotFound() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourPaxRepository.findById(10L)).thenReturn(Optional.of(tourPax));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.createTourSchedule(1L, createRequestDTO));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    // endregion

    // region getScheduleOptions Tests

    @Test
    void getScheduleOptions_Normal_Success() {
        // Arrange
        User creator = User.builder().id(30L).build();
        tour.setCreatedBy(creator);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(userRepository.findByRoleName("SERVICE_COORDINATOR")).thenReturn(List.of(coordinator));
        when(tourPaxRepository.findByTourId(1L)).thenReturn(List.of(tourPax));
        when(userMapper.toUserBasicDTO(any(User.class))).thenReturn(new UserBasicDTO());

        // Act
        GeneralResponse<TourScheduleOptionsDTO> response = tourScheduleService.getScheduleOptions(1L);

        // Assert
        assertNotNull(response);
        assertEquals(Constants.Message.GET_SCHEDULE_OPTIONS_SUCCESS, response.getMessage());
        TourScheduleOptionsDTO dto = response.getData();
        assertNotNull(dto);
        assertEquals(1L, dto.getTourId());
        assertEquals("Amazing Vietnam Tour", dto.getTourName());
        assertEquals(1, dto.getCoordinators().size());
        assertEquals(1, dto.getTourPaxes().size());
        assertNotNull(dto.getCreatedBy());
        verify(userMapper, times(2)).toUserBasicDTO(any(User.class)); // 1 for coordinator, 1 for creator
    }

    @Test
    void getScheduleOptions_Boundary_EmptyListsAndNullCreator() {
        // Arrange
        tour.setCreatedBy(null);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(userRepository.findByRoleName("SERVICE_COORDINATOR")).thenReturn(Collections.emptyList());
        when(tourPaxRepository.findByTourId(1L)).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<TourScheduleOptionsDTO> response = tourScheduleService.getScheduleOptions(1L);

        // Assert
        assertNotNull(response);
        TourScheduleOptionsDTO dto = response.getData();
        assertNotNull(dto);
        assertTrue(dto.getCoordinators().isEmpty());
        assertTrue(dto.getTourPaxes().isEmpty());
        assertNull(dto.getCreatedBy());
    }

    @Test
    void getScheduleOptions_Abnormal_TourNotFound() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.getScheduleOptions(1L));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        // SỬA LỖI: Sử dụng hằng số từ Constants.java
        assertEquals(Constants.Message.TOUR_NOT_FOUND, exception.getMessage());
    }

    // endregion

    // region getTourSchedules Tests

    @Test
    void getTourSchedules_Normal_Success() {
        // Arrange
        TourSchedule schedule = TourSchedule.builder()
                .id(101L)
                .coordinator(coordinator)
                .tourPax(tourPax)
                .departureDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(4))
                .build();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourScheduleRepository.findByTourId(1L)).thenReturn(List.of(schedule));

        // Act
        GeneralResponse<List<TourScheduleManagerDTO>> response = tourScheduleService.getTourSchedules(1L);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        TourScheduleManagerDTO dto = response.getData().get(0);
        assertEquals(101L, dto.getId());
        assertEquals(coordinator.getId(), dto.getCoordinatorId());
        assertEquals(tourPax.getId(), dto.getTourPaxId());
    }

    @Test
    void getTourSchedules_Boundary_NoSchedulesFound() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourScheduleRepository.findByTourId(1L)).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<List<TourScheduleManagerDTO>> response = tourScheduleService.getTourSchedules(1L);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void getTourSchedules_Abnormal_TourNotFound() {
        // Arrange
        when(tourRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> tourScheduleService.getTourSchedules(99L));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        // SỬA LỖI: Sử dụng hằng số từ Constants.java
        assertEquals(Constants.Message.TOUR_NOT_FOUND, exception.getMessage());
    }

    // endregion
}