package com.liftit.workout;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutExerciseTest {

    private static final Long ID = 1L;
    private static final Long EXERCISE_ID = 10L;
    private static final Weight WEIGHT = new Weight(100.0, WeightUnit.LBS);
    private static final WorkoutSet SET_1 = new WorkoutSet(1, 10, WEIGHT, null);

    @Test
    void shouldCreateWorkoutExerciseWithEmptySets() {
        // Given / When
        WorkoutExercise we = new WorkoutExercise(ID, EXERCISE_ID, 1, List.of(), null);

        // Then
        assertEquals(ID, we.id());
        assertEquals(EXERCISE_ID, we.exerciseId());
        assertEquals(1, we.order());
        assertEquals(0, we.setCount());
        assertNull(we.notes());
    }

    @Test
    void shouldCreateWorkoutExerciseWithNotes() {
        // Given / When
        WorkoutExercise we = new WorkoutExercise(ID, EXERCISE_ID, 1, List.of(), "Focus on form");

        // Then
        assertEquals("Focus on form", we.notes());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutExercise(null, EXERCISE_ID, 1, List.of(), null));
    }

    @Test
    void shouldThrowWhenExerciseIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutExercise(ID, null, 1, List.of(), null));
    }

    @Test
    void shouldThrowWhenOrderIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutExercise(ID, EXERCISE_ID, 0, List.of(), null));
    }

    @Test
    void shouldThrowWhenSetsIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutExercise(ID, EXERCISE_ID, 1, null, null));
    }

    @Test
    void shouldReturnUnmodifiableSets() {
        // Given
        WorkoutExercise we = new WorkoutExercise(ID, EXERCISE_ID, 1, List.of(SET_1), null);

        // When / Then
        assertThrows(UnsupportedOperationException.class, () -> we.sets().add(SET_1));
    }

    @Test
    void shouldDefensiveCopyInputSets() {
        // Given
        List<WorkoutSet> mutableSets = new ArrayList<>();
        mutableSets.add(SET_1);
        WorkoutExercise we = new WorkoutExercise(ID, EXERCISE_ID, 1, mutableSets, null);

        // When — mutate original list
        mutableSets.clear();

        // Then — entity is not affected
        assertEquals(1, we.setCount());
    }

    @Test
    void shouldAddSetAndReturnNewInstance() {
        // Given
        WorkoutExercise we = new WorkoutExercise(ID, EXERCISE_ID, 1, List.of(), null);

        // When
        WorkoutExercise updated = we.withSet(SET_1);

        // Then
        assertEquals(0, we.setCount());
        assertEquals(1, updated.setCount());
        assertEquals(SET_1, updated.sets().getFirst());
    }

    @Test
    void shouldThrowWhenAddingNullSet() {
        WorkoutExercise we = new WorkoutExercise(ID, EXERCISE_ID, 1, List.of(), null);
        assertThrows(IllegalArgumentException.class, () -> we.withSet(null));
    }
}
