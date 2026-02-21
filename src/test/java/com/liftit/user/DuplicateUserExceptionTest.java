package com.liftit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateUserExceptionTest {

    @Test
    void shouldCreateExceptionWithAuth0IdMessage() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");

        // When
        DuplicateUserException exception = DuplicateUserException.forAuth0Id(auth0Id);

        // Then
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("auth0|abc123"));
    }

    @Test
    void shouldCreateExceptionWithEmailMessage() {
        // Given
        Email email = Email.of("user@example.com");

        // When
        DuplicateUserException exception = DuplicateUserException.forEmail(email);

        // Then
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("user@example.com"));
    }

    @Test
    void shouldBeRuntimeException() {
        // Given / When
        DuplicateUserException exception = DuplicateUserException.forAuth0Id(Auth0Id.of("auth0|xyz"));

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }
}
