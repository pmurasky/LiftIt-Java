package com.liftit;

import com.liftit.user.exception.DuplicateProfileException;
import com.liftit.user.exception.DuplicateUserException;
import com.liftit.user.exception.UnauthorizedException;
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
