package com.liftit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Auth0IdTest {

    @Test
    void shouldCreateAuth0IdFromValidValue() {
        // Given
        String value = "auth0|abc123";

        // When
        Auth0Id auth0Id = Auth0Id.of(value);

        // Then
        assertEquals("auth0|abc123", auth0Id.value());
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Auth0Id.of(null));
    }

    @Test
    void shouldThrowWhenValueIsBlank() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Auth0Id.of("   "));
    }

    @Test
    void shouldThrowWhenValueIsEmpty() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Auth0Id.of(""));
    }

    @Test
    void shouldConsiderEqualWhenSameValue() {
        // Given
        Auth0Id a = Auth0Id.of("auth0|abc123");
        Auth0Id b = Auth0Id.of("auth0|abc123");

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentValues() {
        // Given
        Auth0Id a = Auth0Id.of("auth0|abc123");
        Auth0Id b = Auth0Id.of("auth0|xyz789");

        // When / Then
        assertNotEquals(a, b);
    }

    @Test
    void shouldPreserveOriginalValueWithoutTrimming() {
        // Given - Auth0 sub claims are exact, must not be modified
        String value = "google-oauth2|12345678901234567890";

        // When
        Auth0Id auth0Id = Auth0Id.of(value);

        // Then
        assertEquals(value, auth0Id.value());
    }
}
