package com.liftit.user;

/**
 * Thrown when a request cannot be authenticated — either the identity
 * header is absent or the identity does not match any known user.
 *
 * <p>Maps to {@code 401 Unauthorized} at the controller layer.
 *
 * <p><strong>Temporary</strong>: This exception exists only while the
 * JWT resource-server filter (epic #14) is not yet implemented. Once
 * Spring Security validates the JWT, unauthenticated requests will be
 * rejected by the filter chain before reaching any controller method.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized");
    }
}
