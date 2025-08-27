package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangePasswordRequestDTO;
import com.fpt.capstone.tourism.dto.request.RefundRequestDTO;
import com.fpt.capstone.tourism.dto.request.UpdateProfileRequestDTO;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.UserProfileResponseDTO;
import com.fpt.capstone.tourism.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    User findById(Long id);
    User findUserByUsername(String username);
    User saveUser(User userDTO);
    Boolean existsByUsername(String userName);
    Boolean exitsByEmail(String email);
    Boolean existsByPhoneNumber(String phone);
    User findUserByEmail(String email);

    GeneralResponse<List<UserBasicDTO>> findFriends(Long userId);

    GeneralResponse<UserBasicDTO> getUserBasic(String username);

    GeneralResponse<String> cancelBooking(Long userId, Long bookingId);

    GeneralResponse<UserProfileResponseDTO> getUserProfile(Long userId);

    GeneralResponse<UserProfileResponseDTO> updateUserProfile(Long userId, UpdateProfileRequestDTO requestDTO, MultipartFile avatarFile);

    GeneralResponse<String> changePassword(Long userId, ChangePasswordRequestDTO requestDTO);

    GeneralResponse<PagingDTO<BookingSummaryDTO>> getBookingHistory(Long userId, Pageable pageable);

    GeneralResponse<String> requestBookingCancellation(Long userId, Long bookingId);

    GeneralResponse<String> submitRefundInfo(Long userId, Long bookingId, RefundRequestDTO requestDTO);
}
