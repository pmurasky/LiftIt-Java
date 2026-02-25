package com.liftit.exercise.exception;

/**
 * Thrown when an exercise with the same name already exists.
 *
 * <p>Maps to {@code 409 Conflict} at the controller layer via
 * {@link com.liftit.GlobalExceptionHandler}.
 */
public class DuplicateExerciseException extends RuntimeException {

    public DuplicateExerciseException(String name) {
        super("Exercise already exists with name: " + name);
    }
}
