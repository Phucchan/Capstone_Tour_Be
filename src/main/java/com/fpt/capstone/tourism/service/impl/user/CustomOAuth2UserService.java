package com.fpt.capstone.tourism.service.impl.user;

import com.fpt.capstone.tourism.model.Role;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.UserRole;
import com.fpt.capstone.tourism.model.enums.Provider;
import com.fpt.capstone.tourism.model.oauth2.OAuthAccount;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.user.UserRoleRepository;
import com.fpt.capstone.tourism.repository.oauth2.OAuthAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final UserRoleRepository userRoleRepository;



    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. Get Registration ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        // 2. Get providerId from OAuth2User
        String providerId;
        if (provider == Provider.GOOGLE) {
            providerId = oAuth2User.getAttribute("sub"); // Google dùng "sub"
        } else if (provider == Provider.FACEBOOK) {
            providerId = oAuth2User.getAttribute("id");  // Facebook dùng "id"
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        // 3. Get email and name from OAuth2User
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // 4. Find or create User and OAuthAccount
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setUsername(email);
                    newUser.setPassword(null); // No password for OAuth
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
        oAuthAccountRepository.findByProviderAndUserId(provider, user.getId())
                .orElseGet(() -> {
                    OAuthAccount account = new OAuthAccount();
                    account.setUser(user);
                    account.setProvider(provider);
                    account.setProviderId(providerId);
                    return oAuthAccountRepository.save(account);
                });

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),      // quyền giữ nguyên
                oAuth2User.getAttributes(),       // thông tin từ provider
                "email" // field nào làm "username" (ID chính)
        );
    }
}