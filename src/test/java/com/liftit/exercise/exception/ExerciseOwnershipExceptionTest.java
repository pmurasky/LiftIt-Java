package com.liftit.exercise.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExerciseOwnershipExceptionTest {

    @Test
    void shouldIncludeExerciseIdInMessage() {
        // Given / When
        ExerciseOwnershipException ex = new ExerciseOwnershipException(99L);

        // Then
        assertNotNull(ex.getMessage());
        assertEquals("You do not have permission to modify exercise: 99", ex.getMessage());
    }
}
