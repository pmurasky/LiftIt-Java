package com.liftit;

import com.liftit.exercise.exception.DuplicateExerciseException;
import com.liftit.exercise.exception.ExerciseNotFoundException;
import com.liftit.exercise.exception.ExerciseOwnershipException;
import com.liftit.user.exception.DuplicateProfileException;
import com.liftit.user.exception.DuplicateUserException;
import com.liftit.user.exception.UnauthorizedException;
import com.liftit.workout.exception.WorkoutAlreadyCompletedException;
import com.liftit.workout.exception.WorkoutNotFoundException;
import com.liftit.workout.exception.WorkoutOwnershipException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that maps domain exceptions to HTTP responses.
 *
 * <p>Centralises error handling so individual controllers remain focused on
 * happy-path logic (Single Responsibility Principle). Each handler produces
 * an empty body with the appropriate HTTP status code.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Void> handleDuplicateUser(DuplicateUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(DuplicateProfileException.class)
    public ResponseEntity<Void> handleDuplicateProfile(DuplicateProfileException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(DuplicateExerciseException.class)
    public ResponseEntity<Void> handleDuplicateExercise(DuplicateExerciseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<Void> handleExerciseNotFound(ExerciseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(ExerciseOwnershipException.class)
    public ResponseEntity<Void> handleExerciseOwnership(ExerciseOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(WorkoutNotFoundException.class)
    public ResponseEntity<Void> handleWorkoutNotFound(WorkoutNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(WorkoutOwnershipException.class)
    public ResponseEntity<Void> handleWorkoutOwnership(WorkoutOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(WorkoutAlreadyCompletedException.class)
    public ResponseEntity<Void> handleWorkoutAlreadyCompleted(WorkoutAlreadyCompletedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Void> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleValidationFailure(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
