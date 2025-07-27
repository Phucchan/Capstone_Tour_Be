package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangePasswordRequestDTO;
import com.fpt.capstone.tourism.dto.request.UpdateProfileRequestDTO;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.UserProfileResponseDTO;
import com.fpt.capstone.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    //postman http://localhost:8080/v1/users/profile?userId=1
    public ResponseEntity<GeneralResponse<UserProfileResponseDTO>> getProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    //postman http://localhost:8080/v1/users/profile?userId=1
    public ResponseEntity<GeneralResponse<UserProfileResponseDTO>> updateProfile(
            @RequestParam Long userId,
            @RequestBody UpdateProfileRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, requestDTO));
    }

    @PutMapping("/change-password")
    public ResponseEntity<GeneralResponse<String>> changePassword(
            @RequestParam Long userId,
            @RequestBody ChangePasswordRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.changePassword(userId, requestDTO));
    }
    @GetMapping("/bookings")
    // postman http://localhost:8080/v1/users/bookings?userId=1&page=0&size=10&sortField=bookingStatus&sortDirection=asc
    public ResponseEntity<GeneralResponse<PagingDTO<BookingSummaryDTO>>> getBookingHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingStatus") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(userService.getBookingHistory(userId, pageable));
    }
}
