package com.timmk22.smartfarming.security;

import com.timmk22.smartfarming.dto.response.JwtExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtExceptionHandlerTest {

    private JwtExceptionHandler jwtExceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        jwtExceptionHandler = new JwtExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("Should handle expired JWT exception")
    void shouldHandleExpiredJwtException() {
        ExpiredJwtException exception = mock(ExpiredJwtException.class);

        ResponseEntity<JwtExceptionResponse> response =
                jwtExceptionHandler.handleExpiredJwtException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Unauthorized");
        assertThat(response.getBody().message()).isEqualTo("The token has expired.");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("Should handle signature exception")
    void shouldHandleSignatureException() {
        SignatureException exception = mock(SignatureException.class);

        ResponseEntity<JwtExceptionResponse> response =
                jwtExceptionHandler.handleSignatureException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Unauthorized");
        assertThat(response.getBody().message()).isEqualTo("The token's signature is invalid.");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("Should handle generic JWT exception")
    void shouldHandleJwtException() {
        JwtException exception = new JwtException("Invalid token");

        ResponseEntity<JwtExceptionResponse> response =
                jwtExceptionHandler.handleJwtException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Unauthorized");
        assertThat(response.getBody().message()).isEqualTo("The token is invalid.");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }
}