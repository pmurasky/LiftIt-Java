package com.liftit.auth.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void shouldCreateInvalidCredentialsException() {
        // When
        AuthenticationException exception = AuthenticationException.invalidCredentials();

        // Then
        assertEquals("Authentication failed: invalid credentials", exception.getMessage());
    }

    @Test
    void shouldCreateNoStrategyFoundException() {
        // When
        AuthenticationException exception = AuthenticationException.noStrategyFound();

        // Then
        assertEquals("Authentication failed: no strategy supports the provided credentials", exception.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        // When
        AuthenticationException exception = AuthenticationException.invalidCredentials();

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }
}
