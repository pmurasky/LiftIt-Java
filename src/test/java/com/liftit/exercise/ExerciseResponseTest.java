package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExerciseResponseTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-01-02T00:00:00Z");

    @Test
    void shouldMapAllFieldsFromDomain() {
        // Given
        Exercise exercise = new Exercise(
                1L, "Bench Press", ExerciseCategoryEnum.STRENGTH,
                Set.of(MuscleEnum.CHEST, MuscleEnum.TRICEPS),
                CREATED_AT, 100L, UPDATED_AT, 100L
        );

        // When
        ExerciseResponse response = ExerciseResponse.from(exercise);

        // Then
        assertEquals(1L, response.id());
        assertEquals("Bench Press", response.name());
        assertEquals(ExerciseCategoryEnum.STRENGTH, response.category());
        assertEquals(Set.of(MuscleEnum.CHEST, MuscleEnum.TRICEPS), response.muscleGroups());
        assertEquals(100L, response.createdBy());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());
    }

    @Test
    void shouldNotExposeUpdatedBy() {
        // Given — updatedBy is an internal audit field and must not appear in the response
        Exercise exercise = new Exercise(
                2L, "Squat", ExerciseCategoryEnum.STRENGTH,
                Set.of(MuscleEnum.THIGHS),
                CREATED_AT, 1L, UPDATED_AT, 99L
        );

        // When
        ExerciseResponse response = ExerciseResponse.from(exercise);

        // Then — response record does not have an updatedBy field (compile-time guarantee)
        assertEquals(2L, response.id());
        assertEquals(1L, response.createdBy());
    }
}
