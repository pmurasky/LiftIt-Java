package com.liftit.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void shouldCreateTokenFromValidValue() {
        // Given
        String tokenValue = "eyJhbGciOiJSUzI1NiJ9.payload.signature";

        // When
        Token token = Token.of(tokenValue);

        // Then
        assertEquals(tokenValue, token.value());
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Token.of(null));
    }

    @Test
    void shouldThrowWhenValueIsBlank() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Token.of("   "));
    }

    @Test
    void shouldThrowWhenValueIsEmpty() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Token.of(""));
    }

    @Test
    void shouldConsiderEqualWhenSameValue() {
        // Given
        Token a = Token.of("same-token");
        Token b = Token.of("same-token");

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentValues() {
        // Given
        Token a = Token.of("token-abc");
        Token b = Token.of("token-xyz");

        // When / Then
        assertNotEquals(a, b);
    }
}
