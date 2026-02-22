package com.liftit.auth.exception;

/**
 * Thrown when an authentication attempt fails for any reason.
 *
 * <p>This is the base exception for all authentication failures in the auth domain.
 * Callers should catch this type when they need to handle any authentication failure
 * without distinguishing between specific causes.
 *
 * <p>Maps to {@code 401 Unauthorized} at the controller layer.
 */
public class AuthenticationException extends RuntimeException {

    private AuthenticationException(String message) {
        super(message);
    }

    /**
     * Creates an exception indicating authentication failed due to invalid credentials.
     *
     * @return a new {@code AuthenticationException}
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Authentication failed: invalid credentials");
    }

    /**
     * Creates an exception indicating no authentication strategy supports the given credentials.
     *
     * @return a new {@code AuthenticationException}
     */
    public static AuthenticationException noStrategyFound() {
        return new AuthenticationException("Authentication failed: no strategy supports the provided credentials");
    }
}
