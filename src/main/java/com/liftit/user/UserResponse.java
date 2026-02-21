package com.liftit.user;

import java.time.Instant;

/**
 * API response representing a provisioned user.
 *
 * <p>Returned by {@code POST /api/v1/users/me} after successful provisioning.
 * Exposes only the fields the frontend needs — internal audit columns
 * ({@code createdBy}, {@code updatedBy}) are not included.
 *
 * @param id        the application-assigned user ID
 * @param auth0Id   the Auth0 subject identifier
 * @param email     the user's email address
 * @param createdAt the timestamp when the user was provisioned
 */
public record UserResponse(Long id, String auth0Id, String email, Instant createdAt) {

    /**
     * Converts a {@link User} domain object to a {@code UserResponse}.
     *
     * @param user the domain user; must not be null
     * @return a new {@code UserResponse}
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.id(),
                user.auth0Id().value(),
                user.email().value(),
                user.createdAt()
        );
    }
}
