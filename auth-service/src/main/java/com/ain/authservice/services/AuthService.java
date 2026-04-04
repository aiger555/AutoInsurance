package com.ain.authservice.services;

import com.ain.authservice.dto.LoginRequestDTO;
import com.ain.authservice.models.User;
import com.ain.authservice.repositories.UserRepository;
import com.ain.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ain.authservice.dto.*;
import com.ain.authservice.models.PasswordResetToken;
import com.ain.authservice.repositories.PasswordResetTokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        log.info("Attempting authentication for email: {}", loginRequestDTO.getEmail());

        try {
            Optional<User> userOptional = userService.findByEmail(loginRequestDTO.getEmail());

            if (userOptional.isEmpty()) {
                log.warn("User not found with email: {}", loginRequestDTO.getEmail());
                return Optional.empty();
            }

            User user = userOptional.get();
            boolean passwordMatches = passwordEncoder.matches(
                    loginRequestDTO.getPassword(),
                    user.getPassword()
            );

            if (!passwordMatches) {
                log.warn("Password mismatch for user: {}", loginRequestDTO.getEmail());
                return Optional.empty();
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            log.info("Successfully generated token for user: {}", loginRequestDTO.getEmail());

            return Optional.of(token);

        } catch (Exception e) {
            log.error("Authentication failed for email: {}", loginRequestDTO.getEmail(), e);
            return Optional.empty();
        }
    }

    public boolean validateToken(String token) {
        log.debug("Validating token: {}", token.substring(0, Math.min(20, token.length())) + "...");
        try {
            jwtUtil.validateToken(token);
            log.debug("Token validation successful");
            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userService.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userService.save(user);

        try {
            emailService.sendWelcomeEmail(savedUser.getEmail());
        } catch (Exception e) {
            log.warn("Failed to send welcome email, but user was created: {}", e.getMessage());
        }

        log.info("User registered successfully: {}", savedUser.getEmail());

        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "User registered successfully. Please check your email for confirmation."
        );
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        log.info("Processing forgot password request for email: {}", request.getEmail());

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        tokenRepository.deleteByUserId(user.getId());

        String token = RandomStringUtils.randomAlphanumeric(32);
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        log.info("Password reset email sent to: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        log.info("Processing password reset with token");

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getEmail());
    }

}