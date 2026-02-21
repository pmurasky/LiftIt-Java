package com.liftit.user;

/**
 * Value object representing an Auth0 subject identifier (the {@code sub} claim).
 *
 * <p>Auth0 subject identifiers are stable, opaque strings that uniquely identify
 * a user within an Auth0 tenant. They are never modified after account creation,
 * even if the user changes their email address.
 *
 * <p>Examples: {@code auth0|abc123}, {@code google-oauth2|12345678901234567890}
 */
public record Auth0Id(String value) {

    /**
     * Creates an {@code Auth0Id} from the given Auth0 subject string.
     *
     * @param value the Auth0 {@code sub} claim; must not be null or blank
     * @return a new {@code Auth0Id}
     * @throws IllegalArgumentException if {@code value} is null or blank
     */
    public Auth0Id {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Auth0Id value must not be null or blank");
        }
    }

    /**
     * Factory method for creating an {@code Auth0Id}.
     *
     * @param value the Auth0 {@code sub} claim
     * @return a new {@code Auth0Id}
     */
    public static Auth0Id of(String value) {
        return new Auth0Id(value);
    }
}
