package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourScheduleCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.ScheduleRepeatType;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private Tour publishedTour;
    private TourPax validTourPax;
    private User validCoordinator;
    private TourScheduleCreateRequestDTO validRequestDTO;

    @BeforeEach
    void setUp() {
        // --- Dữ liệu giả lập ---
        publishedTour = Tour.builder()
                .id(1L)
                .tourStatus(TourStatus.PUBLISHED)
                .durationDays(3)
                .build();

        validTourPax = TourPax.builder()
                .id(10L)
                .tour(publishedTour)
                .sellingPrice(2000000.0)
                .extraHotelCost(500000.0)
                .maxQuantity(20)
                .build();

        validCoordinator = User.builder().id(100L).build();

        validRequestDTO = TourScheduleCreateRequestDTO.builder()
                .tourPaxId(validTourPax.getId())
                .coordinatorId(validCoordinator.getId())
                .departureDate(LocalDateTime.of(2024, 12, 20, 8, 0))
                .repeatType(ScheduleRepeatType.NONE)
                .repeatCount(0)
                .build();
    }

    // =================================================================
    // 1. Valid Input Case
    // =================================================================

    @Test
    @DisplayName("[createTourSchedule] Valid Input: Tạo thành công khi tất cả đầu vào đều hợp lệ")
    void createTourSchedule_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Tạo thành công khi tất cả đầu vào đều hợp lệ.");
        // Arrange
        when(tourRepository.findById(publishedTour.getId())).thenReturn(Optional.of(publishedTour));
        when(tourPaxRepository.findById(validTourPax.getId())).thenReturn(Optional.of(validTourPax));
        when(userRepository.findById(validCoordinator.getId())).thenReturn(Optional.of(validCoordinator));

        // Giả lập hàm save trả về một TourSchedule đã có ID
        when(tourScheduleRepository.save(any(TourSchedule.class))).thenAnswer(invocation -> {
            TourSchedule saved = invocation.getArgument(0);
            saved.setId(1L); // Gán ID giả
            return saved;
        });

        // Act
        GeneralResponse<List<TourScheduleManagerDTO>> response = tourScheduleService.createTourSchedule(publishedTour.getId(), validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(Constants.Message.SCHEDULE_CREATED_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());

        // Verify
        verify(tourScheduleRepository, times(1)).save(any(TourSchedule.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    // =================================================================
    // 2. Invalid Input Cases
    // =================================================================

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi tourId không tồn tại")
    void createTourSchedule_whenTourNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tourId không tồn tại.");
        // Arrange
        Long nonExistentTourId = 99L;
        when(tourRepository.findById(nonExistentTourId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourScheduleService.createTourSchedule(nonExistentTourId, validRequestDTO);
        });

        assertEquals("Tour not found", exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_NOT_FOUND);
    }

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi tourId là null")
    void createTourSchedule_whenTourIdIsNull_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tourId là null.");
        // Arrange
        Long nullTourId = null;
        // Giả lập repository sẽ ném lỗi khi nhận ID là null
        when(tourRepository.findById(nullTourId)).thenThrow(new IllegalArgumentException("ID must not be null!"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            tourScheduleService.createTourSchedule(nullTourId, validRequestDTO);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Tour ID không được là null.");
    }

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi tourPaxId không tồn tại")
    void createTourSchedule_whenTourPaxNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tourPaxId không tồn tại.");
        // Arrange
        validRequestDTO.setTourPaxId(99L); // ID không tồn tại
        when(tourRepository.findById(publishedTour.getId())).thenReturn(Optional.of(publishedTour));
        when(tourPaxRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourScheduleService.createTourSchedule(publishedTour.getId(), validRequestDTO);
        });

        assertEquals(Constants.Message.TOUR_PAX_NOT_FOUND, exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_PAX_NOT_FOUND);
    }

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi tourPaxId là null")
    void createTourSchedule_whenTourPaxIdIsNull_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tourPaxId là null.");
        // Arrange
        validRequestDTO.setTourPaxId(null);
        when(tourRepository.findById(publishedTour.getId())).thenReturn(Optional.of(publishedTour));
        // Giả lập repository sẽ ném lỗi khi nhận ID là null
        when(tourPaxRepository.findById(null)).thenThrow(new IllegalArgumentException("ID must not be null!"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            tourScheduleService.createTourSchedule(publishedTour.getId(), validRequestDTO);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: TourPax ID không được là null.");
    }

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi coordinatorId không tồn tại")
    void createTourSchedule_whenCoordinatorNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi coordinatorId không tồn tại.");
        // Arrange
        validRequestDTO.setCoordinatorId(99L); // ID không tồn tại
        when(tourRepository.findById(publishedTour.getId())).thenReturn(Optional.of(publishedTour));
        when(tourPaxRepository.findById(validTourPax.getId())).thenReturn(Optional.of(validTourPax));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourScheduleService.createTourSchedule(publishedTour.getId(), validRequestDTO);
        });

        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE);
    }

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi coordinatorId là null")
    void createTourSchedule_whenCoordinatorIdIsNull_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi coordinatorId là null.");
        // Arrange
        validRequestDTO.setCoordinatorId(null);
        when(tourRepository.findById(publishedTour.getId())).thenReturn(Optional.of(publishedTour));
        when(tourPaxRepository.findById(validTourPax.getId())).thenReturn(Optional.of(validTourPax));
        // Giả lập repository sẽ ném lỗi khi nhận ID là null
        when(userRepository.findById(null)).thenThrow(new IllegalArgumentException("ID must not be null!"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            tourScheduleService.createTourSchedule(publishedTour.getId(), validRequestDTO);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Coordinator ID không được là null.");
    }

    @Test
    @DisplayName("[createTourSchedule] Invalid Input: Thất bại khi departureDate là null")
    void createTourSchedule_whenDepartureDateIsNull_shouldThrowException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi departureDate là null.");
        // Arrange
        validRequestDTO.setDepartureDate(null);
        validRequestDTO.setRepeatType(ScheduleRepeatType.WEEKLY); // Đặt repeat để kích hoạt logic tính toán ngày
        when(tourRepository.findById(publishedTour.getId())).thenReturn(Optional.of(publishedTour));
        when(tourPaxRepository.findById(validTourPax.getId())).thenReturn(Optional.of(validTourPax));
        when(userRepository.findById(validCoordinator.getId())).thenReturn(Optional.of(validCoordinator));

        // Act & Assert
        // Kỳ vọng một NullPointerException vì code sẽ gọi .plusWeeks() trên một đối tượng null
        assertThrows(NullPointerException.class, () -> {
            tourScheduleService.createTourSchedule(publishedTour.getId(), validRequestDTO);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Ngày khởi hành không được là null.");
    }
}