package com.liftit.workout.exception;

/**
 * Thrown when a workout cannot be found by its ID.
 *
 * <p>Maps to {@code 404 Not Found} at the controller layer via
 * {@link com.liftit.GlobalExceptionHandler}.
 */
public class WorkoutNotFoundException extends RuntimeException {

    public WorkoutNotFoundException(Long id) {
        super("Workout not found: " + id);
    }
}
