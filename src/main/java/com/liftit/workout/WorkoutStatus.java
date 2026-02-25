package com.liftit.workout;

/**
 * Lifecycle status of a {@link Workout}.
 *
 * <p>A workout transitions in one direction only:
 * {@code IN_PROGRESS} → {@code COMPLETED}.
 * Once completed, a workout cannot be modified.
 */
public enum WorkoutStatus {

    /** The workout has been started and is actively being logged. */
    IN_PROGRESS,

    /** The workout has been finished; no further modifications are allowed. */
    COMPLETED
}
