package com.example.kegichivka.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${app.base-url:http://localhost:1923}")
    private String baseUrl;

    @Async
    public void sendVerificationEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("vadimkh17@gmail.com");
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

    @Async
    public void sendAdminActivationEmail(String to, String token, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("vadimkh17@gmail.com");
            helper.setTo(to);
            helper.setSubject("Активация аккаунта администратора");

            // Изменяем на общий путь активации
            String activationLink = baseUrl + "/auth/activate?token=" + token;
            String emailContent = createAdminActivationEmailTemplate(firstName, activationLink);

            helper.setText(emailContent, true);
            mailSender.send(message);

            log.info("Admin activation email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send admin activation email to: {}", to, e);
            throw new RuntimeException("Failed to send admin activation email", e);
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

    private String createAdminActivationEmailTemplate(String firstName, String activationLink) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">Активация аккаунта администратора</h2>
                <p>Уважаемый(ая) %s,</p>
                <p>Благодарим вас за регистрацию в качестве администратора. Для активации вашего аккаунта, пожалуйста, 
                   перейдите по ссылке ниже:</p>
                <p style="margin: 20px 0;">
                    <a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; 
                       text-decoration: none; border-radius: 5px; display: inline-block;">
                        Активировать аккаунт администратора
                    </a>
                </p>
                <p>
                    Если кнопка выше не работает, скопируйте и вставьте следующую ссылку в ваш браузер:<br>
                    <span style="color: #0066cc;">%s</span>
                </p>
                <div style="margin-top: 20px; padding: 20px; background-color: #f8f9fa; border-radius: 5px;">
                    <p style="color: #666; margin: 0;">
                        <strong>Важная информация:</strong>
                        <ul style="margin-top: 10px;">
                            <li>Ссылка действительна в течение 24 часов</li>
                            <li>После активации вы получите доступ к панели администратора</li>
                            <li>По соображениям безопасности не передавайте эту ссылку третьим лицам</li>
                        </ul>
                    </p>
                </div>
                <p style="color: #666; font-size: 14px; margin-top: 20px;">
                    Если вы не регистрировались в качестве администратора, пожалуйста, проигнорируйте это письмо.
                </p>
            </div>
            """.formatted(firstName, activationLink, activationLink);
    }

}