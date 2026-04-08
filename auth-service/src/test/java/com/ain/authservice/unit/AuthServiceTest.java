package com.ain.authservice.unit;

import com.ain.authservice.dto.LoginRequestDTO;
import com.ain.authservice.models.User;
import com.ain.authservice.services.AuthService;
import com.ain.authservice.services.UserService;
import com.ain.authservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDTO loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRole("USER");
    }

    @Test
    void authenticate_ValidCredentials_ReturnsToken() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "USER")).thenReturn("jwt_token_123");

        Optional<String> result = authService.authenticate(loginRequest);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("jwt_token_123");
    }

    @Test
    void authenticate_UserNotFound_ReturnsEmpty() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Optional<String> result = authService.authenticate(loginRequest);

        assertThat(result).isEmpty();
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void authenticate_WrongPassword_ReturnsEmpty() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(false);

        Optional<String> result = authService.authenticate(loginRequest);

        assertThat(result).isEmpty();
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}