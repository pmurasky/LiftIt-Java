package com.liftit.workout.exception;

/**
 * Thrown when a user attempts to modify or delete a workout they do not own.
 *
 * <p>Maps to {@code 403 Forbidden} at the controller layer via
 * {@link com.liftit.GlobalExceptionHandler}.
 */
public class WorkoutOwnershipException extends RuntimeException {

    public WorkoutOwnershipException(Long workoutId, Long userId) {
        super("User " + userId + " does not own workout " + workoutId);
    }
}
