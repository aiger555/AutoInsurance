package com.ain.authservice.controllers;

import com.ain.authservice.dto.LoginRequestDTO;
import com.ain.authservice.dto.LoginResponseDTO;
import com.ain.authservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate token on user login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);
            if (tokenOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String token = tokenOptional.get();
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("=== VALIDATE TOKEN DEBUG ===");
        log.info("Authorization Header: {}", authHeader);

        // ПРОВЕРКА: Должен быть Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header: {}", authHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7); // Убираем "Bearer "
        log.info("Token to validate: {}", token);

        boolean isValid = authService.validateToken(token);
        log.info("Token validation result: {}", isValid);

        return isValid
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}