package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExerciseFilterTest {

    @Test
    void shouldCreateEmptyFilterWithAllNullFields() {
        // When
        ExerciseFilter filter = ExerciseFilter.empty();

        // Then
        assertNull(filter.category());
        assertNull(filter.muscleGroup());
        assertNull(filter.search());
    }

    @Test
    void shouldStoreFilterValues() {
        // Given / When
        ExerciseFilter filter = new ExerciseFilter(
                ExerciseCategoryEnum.STRENGTH, MuscleEnum.CHEST, "bench"
        );

        // Then
        assertEquals(ExerciseCategoryEnum.STRENGTH, filter.category());
        assertEquals(MuscleEnum.CHEST, filter.muscleGroup());
        assertEquals("bench", filter.search());
    }
}
