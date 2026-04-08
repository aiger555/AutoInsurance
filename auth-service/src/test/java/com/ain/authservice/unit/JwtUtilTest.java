
package com.ain.authservice.unit;

import com.ain.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        String testSecret = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdGVzdGluZy1wdXJwb3NlLTI1Ng==";
        jwtUtil = new JwtUtil(testSecret);
    }

    @Test
    void generateToken_ValidInput_ReturnsToken() {
        String token = jwtUtil.generateToken("user@test.com", "ADMIN");

        assertThat(token).isNotNull();
        assertThat(token).contains(".");
    }

    @Test
    void validateToken_CorrectToken_DoesNotThrowException() {
        String token = jwtUtil.generateToken("user@test.com", "USER");

        jwtUtil.validateToken(token);
    }

    @Test
    void validateToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.string";

        assertThatThrownBy(() -> jwtUtil.validateToken(invalidToken))
                .isInstanceOf(JwtException.class);
    }
}