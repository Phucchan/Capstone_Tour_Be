package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.MailServiceDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.Token;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.repository.EmailConfirmationTokenRepository;
import com.fpt.capstone.tourism.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationServiceImplTest {

    @InjectMocks
    private EmailConfirmationServiceImpl emailConfirmationService;

    @Mock
    private EmailConfirmationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    private User user;
    private Token token;

    @BeforeEach
    void setUp() {
        // Set values for @Value annotated fields
        ReflectionTestUtils.setField(emailConfirmationService, "frontendBaseUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(emailConfirmationService, "backendBaseUrl", "http://localhost:8080");

        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .username("testuser")
                .email("test@example.com")
                .build();

        token = Token.builder()
                .id(1L)
                .token(UUID.randomUUID().toString())
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
    }

    // region createEmailConfirmationToken Tests
    @Test
    void createEmailConfirmationToken_Normal_Success() {
        // Arrange
        when(tokenRepository.save(any(Token.class))).thenReturn(token);

        // Act
        Token createdToken = emailConfirmationService.createEmailConfirmationToken(user);

        // Assert
        assertNotNull(createdToken);
        assertEquals(token.getToken(), createdToken.getToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }
    // endregion

    // region sendConfirmationEmail Tests
    @Test
    void sendConfirmationEmail_Normal_Success() throws Exception {
        // Arrange
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        emailConfirmationService.sendConfirmationEmail(user, token);

        // Assert
        verify(emailService, times(1)).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), contentCaptor.capture());
        assertEquals(user.getEmail(), emailCaptor.getValue());
        assertEquals("Xác Nhận Email Viet Travel", subjectCaptor.getValue());
        assertTrue(contentCaptor.getValue().contains("http://localhost:4200/confirm-email?token=" + token.getToken()));
    }

    @Test
    void sendConfirmationEmail_Abnormal_ThrowsException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Mail server is down")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailConfirmationService.sendConfirmationEmail(user, token));

        assertEquals(Constants.Message.TOKEN_ENCRYPTION_FAILED_MESSAGE, exception.getMessage());
    }
    // endregion

    // region sendForgotPasswordEmail Tests
    @Test
    void sendForgotPasswordEmail_Normal_Success() throws Exception {
        // Arrange
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        emailConfirmationService.sendForgotPasswordEmail(user, token);

        // Assert
        verify(emailService, times(1)).sendEmail(eq(user.getEmail()), eq("Đặt Lại Mật Khẩu"), contentCaptor.capture());
        assertTrue(contentCaptor.getValue().contains("http://localhost:8080/reset-password?token=" + token.getToken()));
    }
    // endregion

    // region validateConfirmationToken Tests
    @Test
    void validateConfirmationToken_Normal_Success() {
        // Arrange
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        when(tokenRepository.save(any(Token.class))).thenReturn(token);

        // Act
        Token validatedToken = emailConfirmationService.validateConfirmationToken(token.getToken());

        // Assert
        assertNotNull(validatedToken);
        assertTrue(validatedToken.isUsed());
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void validateConfirmationToken_Abnormal_TokenNotFound() {
        // Arrange
        String nonExistentToken = "non-existent-token";
        when(tokenRepository.findByToken(nonExistentToken)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailConfirmationService.validateConfirmationToken(nonExistentToken));

        assertEquals(Constants.Message.INVALID_CONFIRMATION_TOKEN_MESSAGE, exception.getMessage());
    }

    @Test
    void validateConfirmationToken_Abnormal_TokenExpired() {
        // Arrange
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // Set expiration to the past
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailConfirmationService.validateConfirmationToken(token.getToken()));

        assertEquals(Constants.Message.INVALID_CONFIRMATION_TOKEN_MESSAGE, exception.getMessage());
    }

    @Test
    void validateConfirmationToken_Abnormal_TokenAlreadyUsed() {
        // Arrange
        token.setUsed(true); // Mark as already used
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailConfirmationService.validateConfirmationToken(token.getToken()));

        assertEquals(Constants.Message.TOKEN_USED_MESSAGE, exception.getMessage());
    }
    // endregion

    // region sendAccountServiceProvider Tests
    @Test
    void sendAccountServiceProvider_Normal_Success() throws Exception {
        // Arrange
        String randomPassword = "randomPassword123";
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        emailConfirmationService.sendAccountServiceProvider(user, randomPassword);

        // Assert
        verify(emailService, times(1)).sendEmail(eq(user.getEmail()), anyString(), contentCaptor.capture());
        assertTrue(contentCaptor.getValue().contains("Tài khoản: " + user.getUsername()));
        assertTrue(contentCaptor.getValue().contains("Mật khẩu: " + randomPassword));
    }

    @Test
    void sendAccountServiceProvider_Abnormal_ThrowsException() throws Exception {
        // Arrange
        String randomPassword = "randomPassword123";
        doThrow(new RuntimeException("Mail server is down")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailConfirmationService.sendAccountServiceProvider(user, randomPassword));

        assertEquals(Constants.Message.SEND_EMAIL_ACCOUNT_FAIL, exception.getMessage());
    }
    // endregion

    // region sendMailServiceProvider Tests
    @Test
    void sendMailServiceProvider_Normal_Success() throws Exception {
        // Arrange
        // FIX: Use the builder to create the DTO instance
        MailServiceDTO mailDto = MailServiceDTO.builder()
                .providerEmail("provider@email.com")
                .emailSubject("Test Subject")
                .emailContent("Test Content")
                .build();
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        emailConfirmationService.sendMailServiceProvider(mailDto);

        // Assert
        verify(emailService, times(1)).sendEmail(mailDto.getProviderEmail(), mailDto.getEmailSubject(), mailDto.getEmailContent());
    }

    @Test
    void sendMailServiceProvider_Abnormal_ThrowsException() throws Exception {
        // Arrange
        // FIX: Use the builder to create the DTO instance
        MailServiceDTO mailDto = MailServiceDTO.builder()
                .providerEmail("provider@email.com")
                .emailSubject("Test Subject")
                .emailContent("Test Content")
                .build();
        doThrow(new RuntimeException("Mail server is down")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailConfirmationService.sendMailServiceProvider(mailDto));

        assertEquals(Constants.Message.SEND_EMAIL_ORDER_SERVICE_FAIL, exception.getMessage());
    }
    // endregion
}