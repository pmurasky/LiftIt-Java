package com.liftit.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final Long SYSTEM_ADMIN_ID = 1L;

    @Test
    void shouldCreateUserWithRequiredFields() {
        // Given
        Long id = 100L;
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When
        User user = new User(id, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID);

        // Then
        assertEquals(id, user.id());
        assertEquals(auth0Id, user.auth0Id());
        assertEquals(email, user.email());
        assertEquals(now, user.createdAt());
        assertEquals(SYSTEM_ADMIN_ID, user.createdBy());
        assertEquals(now, user.updatedAt());
        assertEquals(SYSTEM_ADMIN_ID, user.updatedBy());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(null, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID));
    }

    @Test
    void shouldThrowWhenAuth0IdIsNull() {
        // Given
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(100L, null, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID));
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(100L, auth0Id, null, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID));
    }

    @Test
    void shouldThrowWhenCreatedAtIsNull() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(100L, auth0Id, email, null, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID));
    }

    @Test
    void shouldThrowWhenCreatedByIsNull() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(100L, auth0Id, email, now, null, now, SYSTEM_ADMIN_ID));
    }

    @Test
    void shouldThrowWhenUpdatedAtIsNull() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(100L, auth0Id, email, now, SYSTEM_ADMIN_ID, null, SYSTEM_ADMIN_ID));
    }

    @Test
    void shouldThrowWhenUpdatedByIsNull() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new User(100L, auth0Id, email, now, SYSTEM_ADMIN_ID, now, null));
    }

    @Test
    void shouldConsiderEqualWhenSameId() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();
        User a = new User(100L, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID);
        User b = new User(100L, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID);

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentIds() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        Email email = Email.of("alice@example.com");
        Instant now = Instant.now();
        User a = new User(100L, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID);
        User b = new User(101L, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID);

        // When / Then
        assertNotEquals(a, b);
    }
}
