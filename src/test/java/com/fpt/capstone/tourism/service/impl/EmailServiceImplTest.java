package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test cho class EmailServiceImpl.
 * Các bài test này xác minh sự tương tác với JavaMailSender mà không thực sự gửi email.
 */
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        // Khởi tạo các mock object
        MockitoAnnotations.openMocks(this);
        // Không cần mock trường 'fromEmail' vì nó không tồn tại trong class service.
    }

    // region sendEmail (Plain Text) Tests

    /**
     * Trường hợp Normal: Kiểm tra phương thức sendEmail (plain text) thành công.
     */
    @Test
    void sendEmail_success() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String content = "Hello World!";

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, content));

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    /**
     * Trường hợp Abnormal: Kiểm tra phương thức sendEmail (plain text) thất bại.
     */
    @Test
    void sendEmail_fail_throwsException() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String content = "Hello World!";

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        // Cấu hình mock để ném ra MailSendException khi phương thức send được gọi.
        doThrow(new MailSendException("Failed to connect"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        // Implementation của service được mong đợi sẽ bắt MailSendException
        // và bọc nó trong một RuntimeException.
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail(to, subject, content);
        });

        assertEquals(Constants.Message.SEND_EMAIL_ACCOUNT_FAIL, exception.getMessage());
        assertTrue(exception.getCause() instanceof MailSendException);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    // endregion

    // region sendEmailHtml Tests

    /**
     * Trường hợp Normal: Kiểm tra phương thức sendEmailHtml chuẩn bị và gửi email thành công.
     */
    @Test
    void sendEmailHtml_success() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String content = "<h1>Hello World!</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendEmailHtml(to, subject, content));

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    /**
     * Trường hợp Abnormal: Kiểm tra cách service hoạt động khi JavaMailSender
     * không gửi được email và ném ra một ngoại lệ.
     */
    @Test
    void sendEmailHtml_fail_throwsException() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String content = "<h1>Hello World!</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Cấu hình mock để ném ra MailSendException khi phương thức send được gọi.
        doThrow(new MailSendException("Failed to connect to mail server"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmailHtml(to, subject, content);
        });

        assertEquals(Constants.Message.SEND_EMAIL_ACCOUNT_FAIL, exception.getMessage());
        assertTrue(exception.getCause() instanceof MailSendException);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    // endregion
}