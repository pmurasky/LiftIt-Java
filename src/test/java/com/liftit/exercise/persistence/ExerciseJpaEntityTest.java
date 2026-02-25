package com.liftit.exercise.persistence;

import com.liftit.exercise.Exercise;
import com.liftit.exercise.ExerciseCategoryEnum;
import com.liftit.muscle.MuscleEnum;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link ExerciseJpaEntity} domain conversion.
 *
 * <p>Verifies that {@code toDomain()} and {@code fromDomain()} correctly
 * round-trip all fields between the JPA entity and the {@link Exercise} domain record.
 */
class ExerciseJpaEntityTest {

    private static final Long ID = 100L;
    private static final String NAME = "Barbell Squat";
    private static final ExerciseCategoryEnum CATEGORY = ExerciseCategoryEnum.STRENGTH;
    private static final Set<MuscleEnum> MUSCLE_GROUPS = Set.of(MuscleEnum.THIGHS, MuscleEnum.BACK);
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long USER_ID = 1L;

    @Test
    void shouldConvertFromDomainPreservingAllFields() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);

        // When
        ExerciseJpaEntity entity = ExerciseJpaEntity.fromDomain(exercise);
        Exercise result = entity.toDomain();

        // Then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(CATEGORY, result.category());
        assertEquals(MUSCLE_GROUPS, result.muscleGroups());
        assertEquals(NOW, result.createdAt());
        assertEquals(USER_ID, result.createdBy());
        assertEquals(NOW, result.updatedAt());
        assertEquals(USER_ID, result.updatedBy());
    }

    @Test
    void shouldMapIdToNullWhenExerciseIdIsZero() {
        // Given — id=0L signals a new (unsaved) exercise
        Exercise exercise = new Exercise(0L, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);

        // When
        ExerciseJpaEntity entity = ExerciseJpaEntity.fromDomain(exercise);

        // Then — null id allows @GeneratedValue to fire
        assertNull(entity.getId());
    }

    @Test
    void shouldPreserveNonZeroIdOnRoundTrip() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);

        // When
        Exercise result = ExerciseJpaEntity.fromDomain(exercise).toDomain();

        // Then
        assertEquals(ID, result.id());
    }

    @Test
    void shouldRoundTripSingleMuscleGroup() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, Set.of(MuscleEnum.CHEST),
                NOW, USER_ID, NOW, USER_ID);

        // When
        Exercise result = ExerciseJpaEntity.fromDomain(exercise).toDomain();

        // Then
        assertEquals(Set.of(MuscleEnum.CHEST), result.muscleGroups());
    }

    @Test
    void shouldRoundTripAllMuscleGroups() {
        // Given — exercise targeting all 10 muscle groups
        Set<MuscleEnum> allMuscles = Set.of(MuscleEnum.values());
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, allMuscles, NOW, USER_ID, NOW, USER_ID);

        // When
        Exercise result = ExerciseJpaEntity.fromDomain(exercise).toDomain();

        // Then
        assertEquals(allMuscles, result.muscleGroups());
    }

    @Test
    void shouldReturnNonNullDomainFromRoundTrip() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);

        // When
        Exercise result = ExerciseJpaEntity.fromDomain(exercise).toDomain();

        // Then
        assertNotNull(result);
    }
}
