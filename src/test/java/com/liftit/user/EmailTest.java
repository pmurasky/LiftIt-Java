package com.liftit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmailFromValidAddress() {
        // Given
        String address = "user@example.com";

        // When
        Email email = Email.of(address);

        // Then
        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldNormaliseEmailToLowerCase() {
        // Given
        String address = "User@Example.COM";

        // When
        Email email = Email.of(address);

        // Then
        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Email.of(null));
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
    }

    @Test
    void shouldThrowWhenEmailIsEmpty() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Email.of(""));
    }

    @Test
    void shouldThrowWhenEmailHasNoAtSymbol() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Email.of("invalidemail.com"));
    }

    @Test
    void shouldThrowWhenEmailHasNoLocalPart() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Email.of("@example.com"));
    }

    @Test
    void shouldThrowWhenEmailHasNoDomain() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> Email.of("user@"));
    }

    @Test
    void shouldConsiderEqualWhenSameNormalisedValue() {
        // Given
        Email a = Email.of("User@Example.com");
        Email b = Email.of("user@example.com");

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentAddresses() {
        // Given
        Email a = Email.of("alice@example.com");
        Email b = Email.of("bob@example.com");

        // When / Then
        assertNotEquals(a, b);
    }
}
