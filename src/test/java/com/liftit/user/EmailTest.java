package com.liftit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmailWithValidAddress() {
        // Given / When
        Email email = new Email("alice@example.com");

        // Then
        assertEquals("alice@example.com", email.value());
    }

    @Test
    void shouldNormaliseEmailToLowercase() {
        // Given / When
        Email email = new Email("Alice@Example.COM");

        // Then
        assertEquals("alice@example.com", email.value());
    }

    @Test
    void shouldStripWhitespaceFromEmail() {
        // Given / When
        Email email = new Email("  alice@example.com  ");

        // Then
        assertEquals("alice@example.com", email.value());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void shouldThrowWhenEmailHasNoAtSign() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> new Email("notanemail"));
    }

    @Test
    void shouldThrowWhenEmailHasEmptyLocalPart() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
    }

    @Test
    void shouldThrowWhenEmailHasNoDotInDomain() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> new Email("alice@nodot"));
    }

    @Test
    void shouldBeEqualWhenSameNormalisedValue() {
        // Given / When
        Email e1 = new Email("Alice@Example.COM");
        Email e2 = new Email("alice@example.com");

        // Then
        assertEquals(e1, e2);
    }
}
