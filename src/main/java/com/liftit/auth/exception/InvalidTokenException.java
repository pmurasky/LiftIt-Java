package com.liftit.auth.exception;

/**
 * Thrown when a token is present but cannot be validated — for example when it
 * is malformed, expired, or its signature does not verify.
 *
 * <p>This is distinct from {@link AuthenticationException} in that a token was
 * provided but is not trustworthy, rather than credentials being wrong.
 *
 * <p>Maps to {@code 401 Unauthorized} at the controller layer.
 */
public final class InvalidTokenException extends RuntimeException {

    private InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Creates an exception indicating the token has expired.
     *
     * @return a new {@code InvalidTokenException}
     */
    public static InvalidTokenException expired() {
        return new InvalidTokenException("Token is expired");
    }

    /**
     * Creates an exception indicating the token is malformed and cannot be parsed.
     *
     * @return a new {@code InvalidTokenException}
     */
    public static InvalidTokenException malformed() {
        return new InvalidTokenException("Token is malformed");
    }

    /**
     * Creates an exception indicating the token signature does not verify.
     *
     * @return a new {@code InvalidTokenException}
     */
    public static InvalidTokenException invalidSignature() {
        return new InvalidTokenException("Token signature is invalid");
    }
}
