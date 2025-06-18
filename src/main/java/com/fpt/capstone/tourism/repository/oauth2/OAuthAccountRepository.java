package com.fpt.capstone.tourism.repository.oauth2;

import com.fpt.capstone.tourism.model.enums.Provider;
import com.fpt.capstone.tourism.model.oauth2.OAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndUserId(Provider provider, Long userId);
}