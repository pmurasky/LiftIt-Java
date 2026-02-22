package com.liftit.auth.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidTokenExceptionTest {

    @Test
    void shouldCreateExpiredTokenException() {
        // When
        InvalidTokenException exception = InvalidTokenException.expired();

        // Then
        assertEquals("Token is expired", exception.getMessage());
    }

    @Test
    void shouldCreateMalformedTokenException() {
        // When
        InvalidTokenException exception = InvalidTokenException.malformed();

        // Then
        assertEquals("Token is malformed", exception.getMessage());
    }

    @Test
    void shouldCreateInvalidSignatureException() {
        // When
        InvalidTokenException exception = InvalidTokenException.invalidSignature();

        // Then
        assertEquals("Token signature is invalid", exception.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        // When
        InvalidTokenException exception = InvalidTokenException.expired();

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }
}
