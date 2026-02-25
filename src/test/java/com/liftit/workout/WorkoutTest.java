package com.liftit.workout;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutTest {

    private static final Long ID = 1L;
    private static final Long USER_ID = 100L;
    private static final Instant STARTED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    private Workout buildWorkout() {
        return new Workout(ID, USER_ID, STARTED_AT, null,
                WorkoutStatus.IN_PROGRESS, null, List.of(),
                NOW, 1L, NOW, 1L);
    }

    private WorkoutExercise buildWorkoutExercise() {
        return new WorkoutExercise(1L, 10L, 1, List.of(), null);
    }

    // --- construction ---

    @Test
    void shouldCreateInProgressWorkoutWithNoExercises() {
        // Given / When
        Workout workout = buildWorkout();

        // Then
        assertEquals(ID, workout.id());
        assertEquals(USER_ID, workout.userId());
        assertEquals(WorkoutStatus.IN_PROGRESS, workout.status());
        assertNull(workout.completedAt());
        assertTrue(workout.exercises().isEmpty());
        assertTrue(workout.isInProgress());
    }

    @Test
    void shouldThrowWhenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout(ID, null, STARTED_AT, null,
                        WorkoutStatus.IN_PROGRESS, null, List.of(), NOW, 1L, NOW, 1L));
    }

    @Test
    void shouldThrowWhenStartedAtIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout(ID, USER_ID, null, null,
                        WorkoutStatus.IN_PROGRESS, null, List.of(), NOW, 1L, NOW, 1L));
    }

    @Test
    void shouldThrowWhenStatusIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout(ID, USER_ID, STARTED_AT, null,
                        null, null, List.of(), NOW, 1L, NOW, 1L));
    }

    @Test
    void shouldThrowWhenExercisesIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout(ID, USER_ID, STARTED_AT, null,
                        WorkoutStatus.IN_PROGRESS, null, null, NOW, 1L, NOW, 1L));
    }

    // --- exercises list defensiveness ---

    @Test
    void shouldReturnUnmodifiableExercises() {
        // Given
        WorkoutExercise we = buildWorkoutExercise();
        Workout workout = new Workout(ID, USER_ID, STARTED_AT, null,
                WorkoutStatus.IN_PROGRESS, null, List.of(we), NOW, 1L, NOW, 1L);

        // When / Then
        assertThrows(UnsupportedOperationException.class, () -> workout.exercises().add(we));
    }

    @Test
    void shouldDefensiveCopyInputExercises() {
        // Given
        WorkoutExercise we = buildWorkoutExercise();
        List<WorkoutExercise> mutableList = new ArrayList<>();
        mutableList.add(we);
        Workout workout = new Workout(ID, USER_ID, STARTED_AT, null,
                WorkoutStatus.IN_PROGRESS, null, mutableList, NOW, 1L, NOW, 1L);

        // When
        mutableList.clear();

        // Then
        assertEquals(1, workout.exercises().size());
    }

    // --- withExercise ---

    @Test
    void shouldAddExerciseToInProgressWorkout() {
        // Given
        Workout workout = buildWorkout();
        WorkoutExercise exercise = buildWorkoutExercise();

        // When
        Workout updated = workout.withExercise(exercise);

        // Then
        assertEquals(0, workout.exercises().size());
        assertEquals(1, updated.exercises().size());
    }

    @Test
    void shouldThrowWhenAddingExerciseToCompletedWorkout() {
        // Given
        Workout completed = buildWorkout().complete();

        // When / Then
        assertThrows(IllegalStateException.class, () -> completed.withExercise(buildWorkoutExercise()));
    }

    @Test
    void shouldThrowWhenAddingNullExercise() {
        assertThrows(IllegalArgumentException.class, () -> buildWorkout().withExercise(null));
    }

    // --- complete ---

    @Test
    void shouldCompleteInProgressWorkout() {
        // Given
        Workout workout = buildWorkout();

        // When
        Workout completed = workout.complete();

        // Then
        assertEquals(WorkoutStatus.COMPLETED, completed.status());
        assertNotNull(completed.completedAt());
        assertFalse(completed.isInProgress());
    }

    @Test
    void shouldThrowWhenCompletingAlreadyCompletedWorkout() {
        // Given
        Workout completed = buildWorkout().complete();

        // When / Then
        assertThrows(IllegalStateException.class, completed::complete);
    }

    // --- totalSetCount ---

    @Test
    void shouldReturnZeroTotalSetCountWithNoExercises() {
        assertEquals(0, buildWorkout().totalSetCount());
    }

    @Test
    void shouldReturnTotalSetCountAcrossAllExercises() {
        // Given
        Weight w = new Weight(100.0, WeightUnit.LBS);
        WorkoutSet s1 = new WorkoutSet(1, 10, w, null);
        WorkoutSet s2 = new WorkoutSet(2, 8, w, null);
        WorkoutExercise ex1 = new WorkoutExercise(1L, 10L, 1, List.of(s1, s2), null);
        WorkoutExercise ex2 = new WorkoutExercise(2L, 11L, 2, List.of(s1), null);
        Workout workout = new Workout(ID, USER_ID, STARTED_AT, null,
                WorkoutStatus.IN_PROGRESS, null, List.of(ex1, ex2), NOW, 1L, NOW, 1L);

        // When / Then
        assertEquals(3, workout.totalSetCount());
    }
}
