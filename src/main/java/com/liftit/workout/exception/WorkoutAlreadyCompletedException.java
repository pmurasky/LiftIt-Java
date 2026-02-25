package com.liftit.workout.exception;

/**
 * Thrown when a mutating operation is attempted on an already-completed workout.
 *
 * <p>Maps to {@code 409 Conflict} at the controller layer via
 * {@link com.liftit.GlobalExceptionHandler}.
 */
public class WorkoutAlreadyCompletedException extends RuntimeException {

    public WorkoutAlreadyCompletedException(Long workoutId) {
        super("Workout " + workoutId + " is already completed");
    }
}
