package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RequestBookingVerificationService {

    private final JavaMailSender mailSender;

    private final Map<String, VerificationInfo> codes = new ConcurrentHashMap<>();

    public void sendCode(String email) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        codes.put(email, new VerificationInfo(code, LocalDateTime.now().plusMinutes(5)));

        String subject = "🌴 Xác Thực Yêu Cầu Đặt Tour - Du Lịch Trải Nghiệm";

        String htmlContent = """
        <div style="font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px;">
            <div style="max-width: 500px; margin: auto; background-color: #ffffff; border-radius: 10px; 
                        box-shadow: 0 4px 8px rgba(0,0,0,0.1); overflow: hidden;">
                
                <div style="background: linear-gradient(90deg, #28a745, #20c997); padding: 15px; text-align: center; color: white;">
                    <h2 style="margin: 0;">🌍 Du Lịch Đi Đâu</h2>
                </div>

                <div style="padding: 20px; text-align: center;">
                    <h3>Xin chào,</h3>
                    <p>Cảm ơn bạn đã gửi yêu cầu đặt tour riêng với chúng tôi.</p>
                    <p>Vui lòng sử dụng mã xác thực dưới đây để hoàn tất yêu cầu của bạn:</p>
                    <h1 style="letter-spacing: 5px; font-size: 32px; color: #28a745; margin: 20px 0;">%s</h1>
                    <p>Mã này sẽ hết hạn sau <strong>5 phút</strong>.</p>
                </div>

                <div style="background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #555;">
                    Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.
                </div>
            </div>
        </div>
        """.formatted(code);

        sendHtmlEmail(email, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email", e);
        }
    }

    public boolean verifyCode(String email, String code) {
        VerificationInfo info = codes.get(email);
        if (info == null) {
            return false;
        }
        if (info.expiry.isBefore(LocalDateTime.now())) {
            codes.remove(email);
            return false;
        }
        return info.code.equals(code);
    }

    private static class VerificationInfo {
        final String code;
        final LocalDateTime expiry;

        VerificationInfo(String code, LocalDateTime expiry) {
            this.code = code;
            this.expiry = expiry;
        }
    }
}
