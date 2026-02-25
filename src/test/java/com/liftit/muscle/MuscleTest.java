package com.liftit.muscle;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MuscleTest {

    private static final Long ID = 1L;
    private static final String NAME = "Quadriceps";
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Test
    void shouldCreateMuscleWithAllFields() {
        // When
        Muscle muscle = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // Then
        assertEquals(ID, muscle.id());
        assertEquals(NAME, muscle.name());
        assertEquals(NOW, muscle.createdAt());
        assertEquals(SYSTEM_USER_ID, muscle.createdBy());
        assertEquals(NOW, muscle.updatedAt());
        assertEquals(SYSTEM_USER_ID, muscle.updatedBy());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(null, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(ID, null, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(ID, "  ", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenCreatedAtIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(ID, NAME, null, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenCreatedByIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(ID, NAME, NOW, null, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenUpdatedAtIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, null, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenUpdatedByIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, null));
    }

    @Test
    void shouldConsiderEqualWhenSameFields() {
        // Given
        Muscle a = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        Muscle b = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentIds() {
        // Given
        Muscle a = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        Muscle b = new Muscle(2L, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When / Then
        assertNotEquals(a, b);
    }
}
