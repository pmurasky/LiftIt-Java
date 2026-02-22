package com.liftit.auth;

import com.liftit.auth.exception.AuthenticationException;
import com.liftit.auth.exception.InvalidTokenException;

/**
 * Contract for authenticating clients and validating tokens.
 *
 * <p>The service delegates to the appropriate {@link AuthenticationStrategy} based on
 * the type of credentials presented. This keeps the service open for extension (new
 * auth methods) without modification (OCP).
 *
 * <p>Callers depend on this interface rather than any concrete implementation
 * (Dependency Inversion Principle). The Spring Security JWT resource-server filter
 * (epic #14) will be the primary caller.
 *
 * <p>Example usage:
 * <pre>
 *   Credentials credentials = Credentials.bearer(rawJwt);
 *   AuthenticationResult result = authService.authenticate(credentials);
 *   Auth0Id principal = result.auth0Id();
 * </pre>
 */
public interface AuthenticationService {

    /**
     * Authenticates a client using the provided credentials.
     *
     * <p>Delegates to the first {@link AuthenticationStrategy} that
     * {@link AuthenticationStrategy#supports(Credentials) supports} the credential type.
     *
     * @param credentials the credentials to authenticate; must not be null
     * @return a successful {@link AuthenticationResult} containing the validated token
     *         and resolved {@link com.liftit.user.Auth0Id} of the principal
     * @throws AuthenticationException if no strategy supports the credentials, or if
     *                                 the credentials are invalid
     * @throws InvalidTokenException   if the token is present but malformed, expired,
     *                                 or the signature does not verify
     * @throws IllegalArgumentException if {@code credentials} is null
     */
    AuthenticationResult authenticate(Credentials credentials);

    /**
     * Invalidates the given token, preventing future use.
     *
     * <p>For stateless JWT implementations this may be a no-op (tokens expire naturally),
     * or it may add the token to a denylist depending on the implementation.
     *
     * @param token the token to invalidate; must not be null
     * @throws IllegalArgumentException if {@code token} is null
     */
    void logout(Token token);

    /**
     * Validates whether the given token is currently valid.
     *
     * <p>A token is valid if it is well-formed, not expired, its signature verifies,
     * and it has not been invalidated via {@link #logout(Token)}.
     *
     * @param token the token to validate; must not be null
     * @return {@code true} if the token is valid; {@code false} otherwise
     * @throws IllegalArgumentException if {@code token} is null
     */
    boolean validateToken(Token token);
}
