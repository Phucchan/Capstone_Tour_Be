package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.tour.CheckInDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserPoint;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.CheckIn;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.CheckInRepository;
import com.fpt.capstone.tourism.repository.user.UserPointRepository;
import com.fpt.capstone.tourism.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @InjectMocks
    private CheckInServiceImpl checkInService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CheckInRepository checkInRepository;
    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private S3Service s3Service;

    private User user;
    private Booking completedBooking;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Set a value for the @Value field
        ReflectionTestUtils.setField(checkInService, "bucketUrl", "http://s3.bucket.url");

        user = User.builder().id(1L).build();
        completedBooking = Booking.builder()
                .id(10L)
                .user(user)
                .bookingStatus(BookingStatus.COMPLETED)
                .build();

        // SỬA LỖI: Chỉ khởi tạo mock, không giả lập (stub) ở đây.
        // Việc giả lập sẽ được thực hiện trong từng test case cụ thể.
        mockFile = mock(MultipartFile.class);
    }

    // =================================================================
    // NHÓM 1: CÁC TRƯỜNG HỢP TEST CHO addCheckIn
    // =================================================================

    @Test
    @DisplayName("[addCheckIn] Valid Input: Thêm check-in thành công và nhận được điểm")
    void addCheckIn_whenFirstTime_shouldSucceedAndAwardPoints() {
        System.out.println("Test Case: Valid Input - Thêm check-in thành công và nhận được điểm.");
        // Arrange
        String fileKey = "albums/image1.jpg";
        CheckIn savedCheckIn = CheckIn.builder()
                .id(100L)
                .booking(completedBooking)
                .imageUrl("http://s3.bucket.url/" + fileKey)
                .pointsEarned(1)
                .build();
        savedCheckIn.setCreatedAt(LocalDateTime.now());

        // Giả lập các dependency cần thiết cho luồng thành công
        when(mockFile.isEmpty()).thenReturn(false); // Giả lập file không rỗng
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(completedBooking));
        when(s3Service.uploadFile(mockFile, "albums")).thenReturn(fileKey);
        when(checkInRepository.countByBooking_IdAndPointsEarnedGreaterThan(10L, 0)).thenReturn(0L);
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(savedCheckIn);
        when(userPointRepository.save(any(UserPoint.class))).thenReturn(new UserPoint());

        // Act
        GeneralResponse<CheckInDTO> response = checkInService.addCheckIn(1L, 10L, mockFile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.ADD_CHECKIN_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(100L, response.getData().getId());
        assertEquals(1, response.getData().getPointsEarned(), "Phải nhận được 1 điểm cho lần check-in này.");

        // Verify
        verify(bookingRepository, times(1)).findById(10L);
        verify(s3Service, times(1)).uploadFile(mockFile, "albums");
        verify(checkInRepository, times(1)).countByBooking_IdAndPointsEarnedGreaterThan(10L, 0);
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
        verify(userPointRepository, times(1)).save(any(UserPoint.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[addCheckIn] Valid Input: Thêm check-in thành công nhưng không nhận điểm (đã đạt giới hạn)")
    void addCheckIn_whenLimitReached_shouldSucceedWithoutAwardingPoints() {
        System.out.println("Test Case: Valid Input - Thêm check-in thành công nhưng không nhận điểm.");
        // Arrange
        CheckIn savedCheckIn = CheckIn.builder().id(101L).pointsEarned(0).build();

        when(mockFile.isEmpty()).thenReturn(false); // Giả lập file không rỗng
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(completedBooking));
        when(s3Service.uploadFile(mockFile, "albums")).thenReturn("albums/image11.jpg");
        when(checkInRepository.countByBooking_IdAndPointsEarnedGreaterThan(10L, 0)).thenReturn(10L);
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(savedCheckIn);

        // Act
        GeneralResponse<CheckInDTO> response = checkInService.addCheckIn(1L, 10L, mockFile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(0, response.getData().getPointsEarned(), "Không được nhận điểm khi đã đạt giới hạn.");

        // Verify
        verify(userPointRepository, never()).save(any(UserPoint.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[addCheckIn] Invalid Input: Thất bại khi không tìm thấy booking")
    void addCheckIn_whenBookingNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi không tìm thấy booking.");
        // Arrange
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            checkInService.addCheckIn(1L, 99L, mockFile);
        });
        assertEquals(Constants.Message.BOOKING_NOT_FOUND, exception.getResponseMessage());

        // Verify
        verify(s3Service, never()).uploadFile(any(), any());
        verify(checkInRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.BOOKING_NOT_FOUND);
    }

    @Test
    @DisplayName("[addCheckIn] Invalid Input: Thất bại khi người dùng không sở hữu booking")
    void addCheckIn_whenUserDoesNotOwnBooking_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi người dùng không sở hữu booking.");
        // Arrange
        Long wrongUserId = 2L;
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(completedBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            checkInService.addCheckIn(wrongUserId, 10L, mockFile);
        });
        assertEquals(Constants.Message.NO_PERMISSION_TO_ACCESS, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.NO_PERMISSION_TO_ACCESS);
    }

    @Test
    @DisplayName("[addCheckIn] Invalid Input: Thất bại khi booking chưa hoàn thành")
    void addCheckIn_whenBookingIsNotCompleted_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi booking chưa hoàn thành.");
        // Arrange
        Booking pendingBooking = Booking.builder().id(11L).user(user).bookingStatus(BookingStatus.PENDING).build();
        when(bookingRepository.findById(11L)).thenReturn(Optional.of(pendingBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            checkInService.addCheckIn(1L, 11L, mockFile);
        });
        assertEquals(Constants.Message.NO_PERMISSION_TO_ACCESS, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.NO_PERMISSION_TO_ACCESS);
    }

    @Test
    @DisplayName("[addCheckIn] Invalid Input: Thất bại khi file ảnh là null")
    void addCheckIn_whenFileIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi file ảnh là null.");
        // Arrange
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(completedBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            checkInService.addCheckIn(1L, 10L, null); // Truyền file null
        });
        assertEquals(Constants.Message.ADD_CHECKIN_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.ADD_CHECKIN_FAIL);
    }

    @Test
    @DisplayName("[addCheckIn] Invalid Input: Thất bại khi file ảnh rỗng")
    void addCheckIn_whenFileIsEmpty_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi file ảnh rỗng.");
        // Arrange
        when(mockFile.isEmpty()).thenReturn(true); // Giả lập file rỗng
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(completedBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            checkInService.addCheckIn(1L, 10L, mockFile);
        });
        assertEquals(Constants.Message.ADD_CHECKIN_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.ADD_CHECKIN_FAIL);
    }

    @Test
    @DisplayName("[addCheckIn] Invalid Input: Thất bại khi S3 service ném ra lỗi")
    void addCheckIn_whenS3Fails_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi S3 service ném ra lỗi.");
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false); // Giả lập file không rỗng
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(completedBooking));
        when(s3Service.uploadFile(mockFile, "albums")).thenThrow(new RuntimeException("S3 connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            checkInService.addCheckIn(1L, 10L, mockFile);
        });
        assertEquals(Constants.Message.ADD_CHECKIN_FAIL, exception.getResponseMessage());

        // Verify
        verify(checkInRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.ADD_CHECKIN_FAIL);
    }
}