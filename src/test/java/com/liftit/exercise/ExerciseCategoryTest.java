package com.liftit.exercise;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExerciseCategoryTest {

    private static final Long ID = 1L;
    private static final String NAME = "Strength";
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Test
    void shouldCreateExerciseCategoryWithAllFields() {
        // When
        ExerciseCategory category = new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // Then
        assertEquals(ID, category.id());
        assertEquals(NAME, category.name());
        assertEquals(NOW, category.createdAt());
        assertEquals(SYSTEM_USER_ID, category.createdBy());
        assertEquals(NOW, category.updatedAt());
        assertEquals(SYSTEM_USER_ID, category.updatedBy());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(null, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(ID, null, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(ID, "  ", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenCreatedAtIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(ID, NAME, null, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenCreatedByIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(ID, NAME, NOW, null, NOW, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenUpdatedAtIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, null, SYSTEM_USER_ID));
    }

    @Test
    void shouldThrowWhenUpdatedByIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, null));
    }

    @Test
    void shouldConsiderEqualWhenSameFields() {
        // Given
        ExerciseCategory a = new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        ExerciseCategory b = new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
