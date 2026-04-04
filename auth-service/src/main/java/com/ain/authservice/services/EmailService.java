package com.ain.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Request");

            String emailContent = String.format(
                    "Hello,\n\n" +
                            "You have requested to reset your password.\n\n" +
                            "Your password reset token is: %s\n\n" +
                            "To reset your password, make a POST request to /reset-password with the following JSON:\n" +
                            "{\n" +
                            "  \"token\": \"%s\",\n" +
                            "  \"newPassword\": \"your-new-password\",\n" +
                            "  \"confirmPassword\": \"your-new-password\"\n" +
                            "}\n\n" +
                            "This token will expire in 24 hours.\n\n" +
                            "If you didn't request this, please ignore this email.\n\n" +
                            "Best regards,\nYour App Team",
                    token, token
            );

            message.setText(emailContent);
            mailSender.send(message);

            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to Our Application");

            String emailContent = String.format(
                    "Hello,\n\n" +
                            "Welcome to our application! Your account has been successfully created.\n\n" +
                            "You can now log in using your email and password.\n\n" +
                            "Login endpoint: POST /login\n" +
                            "With JSON: {\"email\": \"%s\", \"password\": \"y-password\"}\n\n" +
                            "Best regards,\n",
                    to
            );

            message.setText(emailContent);
            mailSender.send(message);

            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }
}