package com.liftit.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    @Test
    void shouldCreateBearerCredentials() {
        // Given
        String token = "eyJhbGciOiJSUzI1NiJ9.payload.signature";

        // When
        Credentials credentials = Credentials.bearer(token);

        // Then
        assertEquals(token, credentials.value());
        assertEquals(Credentials.CredentialType.BEARER, credentials.type());
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Credentials.bearer(null));
    }

    @Test
    void shouldThrowWhenValueIsBlank() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Credentials.bearer("   "));
    }

    @Test
    void shouldThrowWhenValueIsEmpty() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Credentials.bearer(""));
    }

    @Test
    void shouldThrowWhenTypeIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Credentials("some-token", null));
    }

    @Test
    void shouldConsiderEqualWhenSameValueAndType() {
        // Given
        Credentials a = Credentials.bearer("token-abc");
        Credentials b = Credentials.bearer("token-abc");

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentValues() {
        // Given
        Credentials a = Credentials.bearer("token-abc");
        Credentials b = Credentials.bearer("token-xyz");

        // When / Then
        assertNotEquals(a, b);
    }
}
