package com.liftit.exercise.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExerciseNotFoundExceptionTest {

    @Test
    void shouldIncludeExerciseIdInMessage() {
        // Given / When
        ExerciseNotFoundException ex = new ExerciseNotFoundException(42L);

        // Then
        assertNotNull(ex.getMessage());
        assertEquals("Exercise not found: 42", ex.getMessage());
    }
}
