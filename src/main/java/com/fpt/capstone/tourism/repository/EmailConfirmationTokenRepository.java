package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.Token;
import com.fpt.capstone.tourism.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}

