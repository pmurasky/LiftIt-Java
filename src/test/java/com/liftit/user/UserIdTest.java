package com.liftit.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void shouldCreateUserIdFromUuid() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        UserId userId = new UserId(uuid);

        // Then
        assertEquals(uuid, userId.value());
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        // Given / When / Then
        assertThrows(NullPointerException.class, () -> new UserId(null));
    }

    @Test
    void shouldGenerateUniqueIds() {
        // When
        UserId id1 = UserId.generate();
        UserId id2 = UserId.generate();

        // Then
        assertNotEquals(id1, id2);
    }

    @Test
    void shouldParseUserIdFromString() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        UserId userId = UserId.of(uuid.toString());

        // Then
        assertEquals(uuid, userId.value());
    }

    @Test
    void shouldThrowWhenParsingNullString() {
        // Given / When / Then
        assertThrows(NullPointerException.class, () -> UserId.of(null));
    }

    @Test
    void shouldThrowWhenParsingInvalidUuidString() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> UserId.of("not-a-uuid"));
    }

    @Test
    void shouldBeEqualWhenSameUuid() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        UserId id1 = new UserId(uuid);
        UserId id2 = new UserId(uuid);

        // Then
        assertEquals(id1, id2);
    }
}
