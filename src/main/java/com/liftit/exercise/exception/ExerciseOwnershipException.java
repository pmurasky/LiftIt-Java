package com.liftit.exercise.exception;

/**
 * Thrown when a user attempts to modify an exercise they do not own.
 *
 * <p>Maps to {@code 403 Forbidden} at the controller layer via
 * {@link com.liftit.GlobalExceptionHandler}.
 */
public class ExerciseOwnershipException extends RuntimeException {

    public ExerciseOwnershipException(Long exerciseId) {
        super("You do not have permission to modify exercise: " + exerciseId);
    }
}
