package com.fpt.capstone.tourism.helper;

import com.fpt.capstone.tourism.helper.IHelper.JwtHelper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.repository.UserRepository;
import com.fpt.capstone.tourism.repository.oauth2.OAuthAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Lấy thông tin email từ OAuth2AuthenticationToken
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Tạo token
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // Tạo JWT token
        String token = jwtHelper.generateToken(user);

        String redirectUrl = frontendBaseUrl + "/login?token=" + token + "&email=" + email;
        response.sendRedirect(redirectUrl);
    }
}
