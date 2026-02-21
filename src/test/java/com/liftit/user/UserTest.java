package com.liftit.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final Username ALICE = new Username("alice");
    private static final Email EMAIL = new Email("alice@example.com");

    @Test
    void shouldCreateUserWithAllFields() {
        // Given
        UserId id = UserId.generate();
        Instant now = Instant.now();

        // When
        User user = new User(id, ALICE, EMAIL, now);

        // Then
        assertEquals(id, user.id());
        assertEquals(ALICE, user.username());
        assertEquals(EMAIL, user.email());
        assertEquals(now, user.createdAt());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class,
            () -> new User(null, ALICE, EMAIL, Instant.now()));
    }

    @Test
    void shouldThrowWhenUsernameIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class,
            () -> new User(UserId.generate(), null, EMAIL, Instant.now()));
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class,
            () -> new User(UserId.generate(), ALICE, null, Instant.now()));
    }

    @Test
    void shouldThrowWhenCreatedAtIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class,
            () -> new User(UserId.generate(), ALICE, EMAIL, null));
    }

    @Test
    void shouldCreateUserViaFactoryMethod() {
        // When
        User user = User.create(ALICE, EMAIL);

        // Then
        assertNotNull(user.id());
        assertEquals(ALICE, user.username());
        assertEquals(EMAIL, user.email());
        assertNotNull(user.createdAt());
    }

    @Test
    void shouldGenerateUniqueIdsViaFactoryMethod() {
        // When
        User u1 = User.create(ALICE, EMAIL);
        User u2 = User.create(ALICE, EMAIL);

        // Then
        assertNotEquals(u1.id(), u2.id());
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        // Given
        UserId id = UserId.generate();
        Instant now = Instant.now();

        // When
        User u1 = new User(id, ALICE, EMAIL, now);
        User u2 = new User(id, ALICE, EMAIL, now);

        // Then
        assertEquals(u1, u2);
    }
}
