package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.Wishlist;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TourRepository tourRepository;

    private User mockUser;
    private Tour mockTour;

    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu mẫu để tái sử dụng
        mockUser = User.builder().id(1L).build();
        mockTour = Tour.builder().id(10L).name("Khám phá Đà Nẵng").build();
    }

    // =================================================================
    // 1. Test Cases for addToWishlist
    // =================================================================

    @Test
    @DisplayName("[addToWishlist] Valid Input: Thêm tour vào wishlist thành công")
    void addToWishlist_whenItemIsNew_shouldSucceedAndSave() {
        System.out.println("Test Case: Valid Input - Thêm tour vào wishlist thành công.");
        // Arrange
        Long userId = 1L;
        Long tourId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mockTour));
        when(wishlistRepository.findByUserIdAndTourId(userId, tourId)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Wishlist> captor = ArgumentCaptor.forClass(Wishlist.class);

        // Act
        GeneralResponse<String> response = wishlistService.addToWishlist(userId, tourId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.CREATE_WISHLIST_SUCCESS, response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(wishlistRepository, times(1)).save(captor.capture());
        Wishlist savedWishlist = captor.getValue();
        assertEquals(userId, savedWishlist.getUser().getId());
        assertEquals(tourId, savedWishlist.getTour().getId());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[addToWishlist] Valid Input: Xử lý thành công khi tour đã có trong wishlist")
    void addToWishlist_whenItemAlreadyExists_shouldSucceedWithoutSaving() {
        System.out.println("Test Case: Valid Input - Xử lý thành công khi tour đã có trong wishlist.");
        // Arrange
        Long userId = 1L;
        Long tourId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mockTour));
        when(wishlistRepository.findByUserIdAndTourId(userId, tourId)).thenReturn(Optional.of(new Wishlist()));

        // Act
        GeneralResponse<String> response = wishlistService.addToWishlist(userId, tourId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.CREATE_WISHLIST_SUCCESS, response.getMessage());

        // Verify
        verify(wishlistRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + "Tour đã tồn tại trong Wishlist.");
    }

    @Test
    @DisplayName("[addToWishlist] Invalid Input: Thất bại khi không tìm thấy User")
    void addToWishlist_whenUserNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi không tìm thấy User.");
        // Arrange
        Long nonExistentUserId = 99L;
        Long tourId = 10L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.addToWishlist(nonExistentUserId, tourId);
        });

        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_NOT_FOUND);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("[addToWishlist] Invalid Input: Thất bại khi không tìm thấy Tour")
    void addToWishlist_whenTourNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi không tìm thấy Tour.");
        // Arrange
        Long userId = 1L;
        Long nonExistentTourId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(nonExistentTourId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.addToWishlist(userId, nonExistentTourId);
        });

        assertEquals(Constants.Message.TOUR_NOT_FOUND, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_NOT_FOUND);
    }

    @Test
    @DisplayName("[addToWishlist] Invalid Input: Thất bại khi userId là null")
    void addToWishlist_whenUserIdIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi userId là null.");
        // Arrange
        Long nullUserId = null;
        Long tourId = 10L;
        when(userRepository.findById(null)).thenThrow(new IllegalArgumentException("ID must not be null"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.addToWishlist(nullUserId, tourId);
        });

        assertEquals(Constants.Message.CREATE_WISHLIST_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: User ID không được là null.");
    }

    @Test
    @DisplayName("[addToWishlist] Invalid Input: Thất bại khi tourId là null")
    void addToWishlist_whenTourIdIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tourId là null.");
        // Arrange
        Long userId = 1L;
        Long nullTourId = null;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(null)).thenThrow(new IllegalArgumentException("ID must not be null"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.addToWishlist(userId, nullTourId);
        });

        assertEquals(Constants.Message.CREATE_WISHLIST_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Tour ID không được là null.");
    }

    @Test
    @DisplayName("[addToWishlist] System Failure: Thất bại khi repository ném ra lỗi lúc lưu")
    void addToWishlist_whenRepositoryFailsOnSave_shouldThrowBusinessException() {
        System.out.println("Test Case: System Failure - Thất bại khi repository ném ra lỗi lúc lưu.");
        // Arrange
        Long userId = 1L;
        Long tourId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mockTour));
        when(wishlistRepository.findByUserIdAndTourId(userId, tourId)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.addToWishlist(userId, tourId);
        });

        assertEquals(Constants.Message.CREATE_WISHLIST_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }

    // =================================================================
    // 2. Test Cases for deleteWishlist
    // =================================================================

    @Test
    @DisplayName("[deleteWishlist] Valid Input: Xóa wishlist thành công")
    void deleteWishlist_whenWishlistExistsAndUserIsOwner_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Xóa wishlist thành công.");
        // Arrange
        Long wishlistId = 20L;
        Long userId = 1L;
        Wishlist mockWishlist = Wishlist.builder().id(wishlistId).user(mockUser).build();

        when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(mockWishlist));
        doNothing().when(wishlistRepository).delete(any(Wishlist.class));

        // Act
        GeneralResponse<String> response = wishlistService.deleteWishlist(wishlistId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.DELETE_WISHLIST_SUCCESS, response.getMessage());

        // Verify
        verify(wishlistRepository, times(1)).findById(wishlistId);
        verify(wishlistRepository, times(1)).delete(mockWishlist);
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[deleteWishlist] Invalid Input: Thất bại khi không tìm thấy Wishlist")
    void deleteWishlist_whenWishlistNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi không tìm thấy Wishlist.");
        // Arrange
        Long nonExistentWishlistId = 99L;
        Long userId = 1L;
        when(wishlistRepository.findById(nonExistentWishlistId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.deleteWishlist(nonExistentWishlistId, userId);
        });

        assertEquals(Constants.Message.WISHLIST_NOT_FOUND, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.WISHLIST_NOT_FOUND);
        verify(wishlistRepository, never()).delete(any());
    }

    @Test
    @DisplayName("[deleteWishlist] Invalid Input: Thất bại khi người dùng không có quyền xóa")
    void deleteWishlist_whenUserIsNotOwner_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi người dùng không có quyền xóa.");
        // Arrange
        Long wishlistId = 20L;
        Long ownerUserId = 1L;
        Long wrongUserId = 2L; // Người dùng khác đang cố gắng xóa

        User ownerUser = User.builder().id(ownerUserId).build();
        Wishlist mockWishlist = Wishlist.builder().id(wishlistId).user(ownerUser).build();

        when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(mockWishlist));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.deleteWishlist(wishlistId, wrongUserId);
        });

        assertEquals(Constants.Message.NO_PERMISSION_TO_DELETE, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.NO_PERMISSION_TO_DELETE);
        verify(wishlistRepository, never()).delete(any());
    }

    @Test
    @DisplayName("[deleteWishlist] Invalid Input: Thất bại khi wishlistId là null")
    void deleteWishlist_whenWishlistIdIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi wishlistId là null.");
        // Arrange
        Long nullWishlistId = null;
        Long userId = 1L;
        when(wishlistRepository.findById(null)).thenThrow(new IllegalArgumentException("ID must not be null"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.deleteWishlist(nullWishlistId, userId);
        });

        assertEquals(Constants.Message.DELETE_WISHLIST_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Wishlist ID không được là null.");
    }

    @Test
    @DisplayName("[deleteWishlist] Invalid Input: Thất bại khi userId là null (dẫn đến lỗi không có quyền)")
    void deleteWishlist_whenUserIdIsNull_shouldThrowNoPermissionException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi userId là null.");
        // Arrange
        Long wishlistId = 20L;
        Long nullUserId = null;
        // mockUser có id là 1L
        Wishlist mockWishlist = Wishlist.builder().id(wishlistId).user(mockUser).build();
        when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(mockWishlist));

        // Act & Assert
        // Service sẽ so sánh một ID hợp lệ (1L) với null, kết quả là `false`.
        // Điều kiện `if` `!false` trở thành `true`, dẫn đến exception NO_PERMISSION_TO_DELETE.
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.deleteWishlist(wishlistId, nullUserId);
        });

        // SỬA LỖI: Kỳ vọng đúng thông báo lỗi mà service trả về trong trường hợp này.
        assertEquals(Constants.Message.NO_PERMISSION_TO_DELETE, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: User ID là null nên không thể xác thực quyền sở hữu.");
    }

    @Test
    @DisplayName("[deleteWishlist] System Failure: Thất bại khi repository ném ra lỗi lúc xóa")
    void deleteWishlist_whenRepositoryFailsOnDelete_shouldThrowBusinessException() {
        System.out.println("Test Case: System Failure - Thất bại khi repository ném ra lỗi lúc xóa.");
        // Arrange
        Long wishlistId = 20L;
        Long userId = 1L;
        Wishlist mockWishlist = Wishlist.builder().id(wishlistId).user(mockUser).build();

        when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(mockWishlist));
        // Giả lập hàm delete ném ra lỗi
        doThrow(new RuntimeException("Database connection error")).when(wishlistRepository).delete(any(Wishlist.class));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            wishlistService.deleteWishlist(wishlistId, userId);
        });

        assertEquals(Constants.Message.DELETE_WISHLIST_FAIL, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
        verify(wishlistRepository, times(1)).delete(mockWishlist);
    }
}