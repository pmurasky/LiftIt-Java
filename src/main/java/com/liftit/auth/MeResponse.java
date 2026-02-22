package com.liftit.auth;

/**
 * API response for {@code GET /api/auth/me}.
 *
 * <p>Returns the identity of the currently authenticated principal as extracted
 * from the JWT {@code sub} claim by the {@link AuthenticationFilter}.
 *
 * @param auth0Id the Auth0 subject identifier of the authenticated user
 */
public record MeResponse(String auth0Id) {

    /**
     * @param auth0Id the Auth0 subject identifier; must not be null or blank
     */
    public MeResponse {
        if (auth0Id == null || auth0Id.isBlank()) {
            throw new IllegalArgumentException("auth0Id must not be blank");
        }
    }
}
