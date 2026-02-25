package com.liftit.workout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutSetTest {

    private static final Weight WEIGHT_100_LBS = new Weight(100.0, WeightUnit.LBS);

    @Test
    void shouldCreateSetWithAllFields() {
        // Given / When
        WorkoutSet set = new WorkoutSet(1, 10, WEIGHT_100_LBS, 8);

        // Then
        assertEquals(1, set.setNumber());
        assertEquals(10, set.reps());
        assertEquals(WEIGHT_100_LBS, set.weight());
        assertEquals(8, set.rpe());
    }

    @Test
    void shouldCreateSetWithNullRpe() {
        // Given / When
        WorkoutSet set = new WorkoutSet(2, 5, WEIGHT_100_LBS, null);

        // Then
        assertEquals(2, set.setNumber());
        assertNull(set.rpe());
    }

    @Test
    void shouldThrowWhenSetNumberIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutSet(0, 10, WEIGHT_100_LBS, null));
    }

    @Test
    void shouldThrowWhenSetNumberIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutSet(-1, 10, WEIGHT_100_LBS, null));
    }

    @Test
    void shouldThrowWhenRepsIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutSet(1, 0, WEIGHT_100_LBS, null));
    }

    @Test
    void shouldThrowWhenWeightIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutSet(1, 10, null, null));
    }

    @Test
    void shouldThrowWhenRpeIsLessThanOne() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutSet(1, 10, WEIGHT_100_LBS, 0));
    }

    @Test
    void shouldThrowWhenRpeIsGreaterThanTen() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkoutSet(1, 10, WEIGHT_100_LBS, 11));
    }

    @Test
    void shouldAcceptRpeBoundaryValues() {
        // RPE 1 and 10 are both valid
        WorkoutSet minRpe = new WorkoutSet(1, 10, WEIGHT_100_LBS, 1);
        WorkoutSet maxRpe = new WorkoutSet(1, 10, WEIGHT_100_LBS, 10);

        assertEquals(1, minRpe.rpe());
        assertEquals(10, maxRpe.rpe());
    }
}
