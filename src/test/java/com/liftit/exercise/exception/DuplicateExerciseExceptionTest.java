package com.liftit.exercise.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DuplicateExerciseExceptionTest {

    @Test
    void shouldIncludeExerciseNameInMessage() {
        // Given / When
        DuplicateExerciseException ex = new DuplicateExerciseException("Bench Press");

        // Then
        assertNotNull(ex.getMessage());
        assertEquals("Exercise already exists with name: Bench Press", ex.getMessage());
    }
}
