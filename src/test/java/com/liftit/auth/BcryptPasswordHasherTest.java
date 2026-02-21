package com.liftit.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BcryptPasswordHasherTest {

    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new BcryptPasswordHasher();
    }

    @Test
    void shouldHashPasswordToNonNullValue() {
        // Given
        String plainPassword = "mySecretPassword123";

        // When
        String hash = passwordHasher.hash(plainPassword);

        // Then
        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        // Given
        String plainPassword = "mySecretPassword123";

        // When
        String hash1 = passwordHasher.hash(plainPassword);
        String hash2 = passwordHasher.hash(plainPassword);

        // Then
        assertNotEquals(hash1, hash2, "BCrypt must generate unique salts per hash");
    }

    @Test
    void shouldVerifyCorrectPassword() {
        // Given
        String plainPassword = "mySecretPassword123";
        String hash = passwordHasher.hash(plainPassword);

        // When
        boolean result = passwordHasher.verify(plainPassword, hash);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldRejectIncorrectPassword() {
        // Given
        String plainPassword = "mySecretPassword123";
        String wrongPassword = "wrongPassword";
        String hash = passwordHasher.hash(plainPassword);

        // When
        boolean result = passwordHasher.verify(wrongPassword, hash);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldRejectEmptyPasswordVerification() {
        // Given
        String plainPassword = "mySecretPassword123";
        String hash = passwordHasher.hash(plainPassword);

        // When
        boolean result = passwordHasher.verify("", hash);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldThrowWhenHashingNullPassword() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hash(null));
    }

    @Test
    void shouldThrowWhenHashingEmptyPassword() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hash(""));
    }

    @Test
    void shouldThrowWhenVerifyingNullPassword() {
        // Given
        String hash = passwordHasher.hash("validPassword");

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.verify(null, hash));
    }

    @Test
    void shouldThrowWhenVerifyingNullHash() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.verify("validPassword", null));
    }

    @Test
    void shouldProduceBcryptFormattedHash() {
        // Given
        String plainPassword = "mySecretPassword123";

        // When
        String hash = passwordHasher.hash(plainPassword);

        // Then
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"),
                "Hash must be BCrypt format starting with $2a$ or $2b$");
    }

    @Test
    void shouldUseCostFactorOfAtLeastTwelve() {
        // Given
        String plainPassword = "mySecretPassword123";

        // When
        String hash = passwordHasher.hash(plainPassword);

        // Then
        int costFactor = Integer.parseInt(hash.split("\\$")[2]);
        assertTrue(costFactor >= 12, "BCrypt cost factor must be at least 12, was: " + costFactor);
    }
}
