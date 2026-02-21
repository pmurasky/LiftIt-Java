package com.liftit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void shouldCreateUsernameWithValidValue() {
        // Given / When
        Username username = new Username("alice");

        // Then
        assertEquals("alice", username.value());
    }

    @Test
    void shouldStripWhitespaceFromUsername() {
        // Given / When
        Username username = new Username("  alice  ");

        // Then
        assertEquals("alice", username.value());
    }

    @Test
    void shouldThrowWhenUsernameIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class, () -> new Username(null));
    }

    @Test
    void shouldThrowWhenUsernameIsBlank() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> new Username("   "));
    }

    @Test
    void shouldThrowWhenUsernameExceedsMaxLength() {
        // Given
        String tooLong = "a".repeat(21);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new Username(tooLong));
    }

    @Test
    void shouldAcceptUsernameAtMaxLength() {
        // Given
        String exactly20 = "a".repeat(20);

        // When
        Username username = new Username(exactly20);

        // Then
        assertEquals(exactly20, username.value());
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        // Given / When
        Username u1 = new Username("alice");
        Username u2 = new Username("alice");

        // Then
        assertEquals(u1, u2);
    }
}
