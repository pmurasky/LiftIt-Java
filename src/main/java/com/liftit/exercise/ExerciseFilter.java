package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;

/**
 * Filter parameters for listing exercises.
 *
 * <p>All fields are optional — a {@code null} value means "no filter on this field".
 * Use {@link #empty()} to create a filter that matches all exercises.
 *
 * @param category    optional category filter
 * @param muscleGroup optional muscle group filter
 * @param search      optional name substring search (case-insensitive)
 */
public record ExerciseFilter(
        ExerciseCategoryEnum category,
        MuscleEnum muscleGroup,
        String search
) {

    /**
     * Returns a filter that matches all exercises (no restrictions).
     *
     * @return an empty filter
     */
    public static ExerciseFilter empty() {
        return new ExerciseFilter(null, null, null);
    }
}
