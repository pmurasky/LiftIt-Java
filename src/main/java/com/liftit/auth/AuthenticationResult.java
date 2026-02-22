package com.liftit.auth;

import com.liftit.user.Auth0Id;

/**
 * Immutable result of a successful authentication operation.
 *
 * <p>Returned by {@link AuthenticationService#authenticate(Credentials)} when
 * credentials are valid. Carries the validated {@link Token} and the resolved
 * {@link Auth0Id} of the authenticated principal, allowing downstream services
 * to look up or provision the local user.
 *
 * <p>An {@code AuthenticationResult} is only ever produced for successful
 * authentication — failures always throw an exception rather than returning
 * a result with an error flag.
 *
 * <p>Example:
 * <pre>
 *   AuthenticationResult result = authService.authenticate(Credentials.bearer(rawJwt));
 *   Auth0Id principal = result.auth0Id();
 * </pre>
 */
public record AuthenticationResult(Token token, Auth0Id auth0Id) {

    /**
     * Canonical constructor — validates that token and auth0Id are non-null.
     *
     * @param token   the validated token; must not be null
     * @param auth0Id the Auth0 subject identifier of the authenticated principal; must not be null
     * @throws IllegalArgumentException if {@code token} or {@code auth0Id} is null
     */
    public AuthenticationResult {
        if (token == null) {
            throw new IllegalArgumentException("AuthenticationResult token must not be null");
        }
        if (auth0Id == null) {
            throw new IllegalArgumentException("AuthenticationResult auth0Id must not be null");
        }
    }

    /**
     * Factory method for creating an {@code AuthenticationResult}.
     *
     * @param token   the validated token
     * @param auth0Id the Auth0 subject identifier of the authenticated principal
     * @return a new {@code AuthenticationResult}
     * @throws IllegalArgumentException if either argument is null
     */
    public static AuthenticationResult of(Token token, Auth0Id auth0Id) {
        return new AuthenticationResult(token, auth0Id);
    }
}
