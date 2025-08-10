package com.fpt.capstone.tourism.controller;


import com.fpt.capstone.tourism.dto.common.TokenDTO;
import com.fpt.capstone.tourism.dto.common.UserDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ForgotPasswordRequestDTO;
import com.fpt.capstone.tourism.dto.request.RegisterRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserInfoResponseDTO;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<TokenDTO>> login(@RequestBody UserDTO userDTO) {
        log.info("Login request received for user: {}", userDTO.getUsername());
        return ResponseEntity.ok(authService.login(userDTO));
    }
    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        Cookie cookie = new Cookie("remember-me", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(authService.logout());
    }


    @GetMapping("/confirm-email")
    public ResponseEntity<GeneralResponse<String>> confirmEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirmEmail(token));
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<UserInfoResponseDTO>> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(authService.register(registerRequestDTO));
    }
    @PostMapping("/forgot-password")
    //postman http://localhost:8080/v1/auth/forgot-password
    public ResponseEntity<GeneralResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.forgotPassword(request.getEmail(), request.getUsername()));
    }

    @GetMapping("/roles")
    public ResponseEntity<GeneralResponse<List<Role>>> getUserRoles() {
        return ResponseEntity.ok(authService.getRoles());
    }
}


