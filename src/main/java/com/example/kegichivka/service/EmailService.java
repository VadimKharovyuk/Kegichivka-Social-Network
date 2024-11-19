package com.example.kegichivka.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final String baseUrl = "http://localhost:1923"; // лучше вынести в properties

    @Async
    public void sendVerificationEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("vadimkh17@gmail.com");  // ваш email
            helper.setTo(to);
            helper.setSubject("Подтверждение регистрации");

            String activationLink = baseUrl + "/auth/activate?token=" + token;
            String emailContent = createEmailTemplate(activationLink);

            helper.setText(emailContent, true);
            mailSender.send(message);

            log.info("Verification email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String createEmailTemplate(String activationLink) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">Подтверждение регистрации</h2>
                <p>Спасибо за регистрацию! Для активации вашего аккаунта, пожалуйста, перейдите по ссылке ниже:</p>
                <p style="margin: 20px 0;">
                    <a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                        Активировать аккаунт
                    </a>
                </p>
                <p style="color: #666; font-size: 14px;">
                    Если кнопка выше не работает, скопируйте и вставьте следующую ссылку в ваш браузер:<br>
                    <span style="color: #0066cc;">%s</span>
                </p>
                <p style="color: #666; font-size: 14px;">
                    Ссылка действительна в течение 24 часов.
                </p>
            </div>
            """.formatted(activationLink, activationLink);
    }

}