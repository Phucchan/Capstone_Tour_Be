package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangePasswordRequestDTO;
import com.fpt.capstone.tourism.dto.request.UpdateProfileRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserProfileResponseDTO;
import com.fpt.capstone.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    //postman http://localhost:8080/v1/users/profile
    public ResponseEntity<GeneralResponse<UserProfileResponseDTO>> getProfile(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<GeneralResponse<UserProfileResponseDTO>> updateProfile(
            @RequestBody UpdateProfileRequestDTO requestDTO,
            Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(userService.updateUserProfile(username, requestDTO));
    }
    @PutMapping("/change-password")
    public ResponseEntity<GeneralResponse<String>> changePassword(
            @RequestBody ChangePasswordRequestDTO requestDTO,
            Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(userService.changePassword(username, requestDTO));
    }
}
