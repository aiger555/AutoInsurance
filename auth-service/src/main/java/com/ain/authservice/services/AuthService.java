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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
}