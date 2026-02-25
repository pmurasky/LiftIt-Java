package com.liftit.workout;

/**
 * Immutable value object representing a single set performed during an exercise.
 *
 * <p>A set captures the reps, weight, rest time, and an optional RPE (Rate of
 * Perceived Exertion on a 1–10 scale). All required fields must be non-null and
 * within valid ranges.
 *
 * @param setNumber the 1-based position of this set within the exercise; must be &gt;= 1
 * @param reps      the number of repetitions performed; must be &gt;= 1
 * @param weight    the weight used; must not be null
 * @param rpe       optional RPE rating 1–10; may be null
 */
public record WorkoutSet(int setNumber, int reps, Weight weight, Integer rpe) {

    /**
     * Compact constructor — validates all required fields.
     *
     * @throws IllegalArgumentException if setNumber &lt; 1, reps &lt; 1, weight is null,
     *                                  or rpe is outside 1–10
     */
    public WorkoutSet {
        if (setNumber < 1) {
            throw new IllegalArgumentException("WorkoutSet.setNumber must be >= 1");
        }
        if (reps < 1) {
            throw new IllegalArgumentException("WorkoutSet.reps must be >= 1");
        }
        if (weight == null) {
            throw new IllegalArgumentException("WorkoutSet.weight must not be null");
        }
        if (rpe != null && (rpe < 1 || rpe > 10)) {
            throw new IllegalArgumentException("WorkoutSet.rpe must be between 1 and 10");
        }
    }
}
