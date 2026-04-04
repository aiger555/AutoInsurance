package com.ain.authservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_ValidCredentials_ReturnsToken() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "USER")).thenReturn("jwt-token-123");

        Optional<String> result = authService.authenticate(request);

        assertTrue(result.isPresent());
        assertEquals("jwt-token-123", result.get());
    }

    @Test
    void authenticate_InvalidPassword_ReturnsEmpty() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        Optional<String> result = authService.authenticate(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_UserNotFound_ReturnsEmpty() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password");

        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<String> result = authService.authenticate(request);

        assertTrue(result.isEmpty());
    }
}