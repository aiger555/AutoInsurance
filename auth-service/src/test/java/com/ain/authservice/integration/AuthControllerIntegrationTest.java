package com.ain.authservice.integration;

import com.ain.authservice.dto.LoginRequestDTO;
import com.ain.authservice.dto.RegisterRequestDTO;
import com.ain.authservice.models.User;
import com.ain.authservice.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setRole("USER");

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_ValidUser_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_ExistingEmail_ReturnsBadRequest() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("testuser@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setRole("USER");
        userRepository.save(existingUser);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_PasswordsDoNotMatch_ReturnsBadRequest() throws Exception {
        registerRequest.setConfirmPassword("differentpassword");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_MissingEmail_ReturnsBadRequest() throws Exception {
        registerRequest.setEmail(null);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WeakPassword_ReturnsBadRequest() throws Exception {
        registerRequest.setPassword("123");
        registerRequest.setConfirmPassword("123");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("USER");
        userRepository.save(user);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_WrongPassword_ReturnsUnauthorized() throws Exception {
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("correctpassword"));
        user.setRole("USER");
        userRepository.save(user);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_UserNotFound_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_EmptyEmail_ReturnsBadRequest() throws Exception {
        loginRequest.setEmail("");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateToken_ValidToken_ReturnsOk() throws Exception {
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("USER");
        userRepository.save(user);

        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void validateToken_InvalidToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/validate")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validateToken_NoAuthHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/validate"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateToken_WrongAuthFormat_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/validate")
                        .header("Authorization", "NotBearer token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void forgotPassword_ValidEmail_ReturnsOk() throws Exception {
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("USER");
        userRepository.save(user);

        String requestJson = "{\"email\": \"testuser@example.com\"}";

        mockMvc.perform(post("/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void forgotPassword_EmailNotFound_StillReturnsOk() throws Exception {
        String requestJson = "{\"email\": \"nonexistent@example.com\"}";

        mockMvc.perform(post("/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void forgotPassword_EmptyEmail_ReturnsBadRequest() throws Exception {
        String requestJson = "{\"email\": \"\"}";

        mockMvc.perform(post("/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}