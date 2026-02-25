package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExerciseTest {

    private static final Long ID = 1L;
    private static final String NAME = "Barbell Squat";
    private static final ExerciseCategoryEnum CATEGORY = ExerciseCategoryEnum.STRENGTH;
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long USER_ID = 1L;

    // Use a valid minimal muscle set for most tests
    private static final Set<MuscleEnum> ONE_MUSCLE = Set.of(MuscleEnum.CHEST);

    @Test
    void shouldCreateExerciseWithAllFields() {
        // When
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID);

        // Then
        assertEquals(ID, exercise.id());
        assertEquals(NAME, exercise.name());
        assertEquals(CATEGORY, exercise.category());
        assertEquals(ONE_MUSCLE, exercise.muscleGroups());
        assertEquals(NOW, exercise.createdAt());
        assertEquals(USER_ID, exercise.createdBy());
        assertEquals(NOW, exercise.updatedAt());
        assertEquals(USER_ID, exercise.updatedBy());
    }

    @Test
    void shouldReturnDefensiveCopyOfMuscleGroups() {
        // Given
        Set<MuscleEnum> mutable = new HashSet<>(ONE_MUSCLE);
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, mutable, NOW, USER_ID, NOW, USER_ID);

        // When — mutate the original set
        mutable.add(MuscleEnum.BACK);

        // Then — exercise muscle groups are unaffected
        assertEquals(1, exercise.muscleGroups().size());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(null, NAME, CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, null, CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, "  ", CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenCategoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, null, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenMuscleGroupsIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, CATEGORY, null, NOW, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenMuscleGroupsIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, CATEGORY, Set.of(), NOW, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenCreatedAtIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, null, USER_ID, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenCreatedByIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, NOW, null, NOW, USER_ID));
    }

    @Test
    void shouldThrowWhenUpdatedAtIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, NOW, USER_ID, null, USER_ID));
    }

    @Test
    void shouldThrowWhenUpdatedByIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, null));
    }

    @Test
    void shouldReturnTrueWhenExerciseTargetsMuscle() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, Set.of(MuscleEnum.CHEST, MuscleEnum.TRICEPS),
                NOW, USER_ID, NOW, USER_ID);

        // When / Then
        assertTrue(exercise.targets(MuscleEnum.CHEST));
        assertTrue(exercise.targets(MuscleEnum.TRICEPS));
    }

    @Test
    void shouldReturnFalseWhenExerciseDoesNotTargetMuscle() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, Set.of(MuscleEnum.CHEST),
                NOW, USER_ID, NOW, USER_ID);

        // When / Then
        assertFalse(exercise.targets(MuscleEnum.BACK));
    }

    @Test
    void shouldReturnTrueForIsStrengthExerciseWhenCategoryIsStrength() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, ExerciseCategoryEnum.STRENGTH, ONE_MUSCLE,
                NOW, USER_ID, NOW, USER_ID);

        // When / Then
        assertTrue(exercise.isStrengthExercise());
    }

    @Test
    void shouldConsiderEqualWhenSameFields() {
        // Given
        Exercise a = new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID);
        Exercise b = new Exercise(ID, NAME, CATEGORY, ONE_MUSCLE, NOW, USER_ID, NOW, USER_ID);

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
