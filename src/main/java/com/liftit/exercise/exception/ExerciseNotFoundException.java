package com.liftit.exercise.exception;

/**
 * Thrown when an exercise cannot be found by its ID.
 *
 * <p>Maps to {@code 404 Not Found} at the controller layer via
 * {@link com.liftit.GlobalExceptionHandler}.
 */
public class ExerciseNotFoundException extends RuntimeException {

    public ExerciseNotFoundException(Long id) {
        super("Exercise not found: " + id);
    }
}
