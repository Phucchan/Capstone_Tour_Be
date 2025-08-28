package com.fpt.capstone.tourism.helper;

import com.fpt.capstone.tourism.helper.IHelper.JwtHelper;
import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.model.enums.Provider;
import com.fpt.capstone.tourism.model.enums.RoleName;
import com.fpt.capstone.tourism.model.oauth2.OAuthAccount;
import com.fpt.capstone.tourism.repository.oauth2.OAuthAccountRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final OAuthAccountRepository oAuthAccountRepository;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Lấy thông tin email từ OAuth2AuthenticationToken
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setUsername(email);
                    newUser.setPassword("Motconvit!"); // No password for OAuth
                    User savedUser =  userRepository.save(newUser);

                    UserRole userRole = UserRole.builder()
                            .user(savedUser)
                            .role(Role.builder().id(1L).build())
                            .deleted(false)
                            .build();

                    userRoleRepository.save(userRole);

                    return savedUser;
                });


        // 5. Find or create OAuthAccount
        oAuthAccountRepository.findByProviderAndUserId(Provider.GOOGLE, user.getId())
                .orElseGet(() -> {
                    OAuthAccount account = new OAuthAccount();
                    account.setUser(user);
                    account.setProvider(Provider.GOOGLE);
                    account.setProviderId(oAuth2User.getAttribute("sub")); // Google dùng "sub"
                    return oAuthAccountRepository.save(account);
                });

        // Tạo token
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found: " + email));



        // Tạo JWT token
        String token = jwtHelper.generateToken(user);
        String path = resolvePath(user);
        String redirectUrl = frontendBaseUrl + path + "?token=" + token + "&email=" + email;
        response.sendRedirect(redirectUrl);
    }
    private String resolvePath(User user) {
        return user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .map(this::mapRoleToPath)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("/login");
    }

    private String mapRoleToPath(String role) {
        try {
            return switch (RoleName.valueOf(role)) {
                case ADMIN -> "/admin";
                case SELLER -> "/seller";
                case MARKETING_MANAGER -> "/marketing";
                case BUSINESS_DEPARTMENT -> "/business";
                case SERVICE_COORDINATOR -> "/coordinator";
                case ACCOUNTANT -> "/accountant";
                default -> null;
            };
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
