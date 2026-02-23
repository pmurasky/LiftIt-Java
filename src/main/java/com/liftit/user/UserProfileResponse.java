package com.liftit.user;

import java.time.Instant;
import java.time.LocalDate;

/**
 * API response representing a user profile.
 *
 * <p>Returned by {@code POST /api/v1/users/me/profile} (201 Created) and
 * {@code GET /api/v1/users/me/profile} (200 OK).
 * Internal audit columns ({@code createdBy}, {@code updatedBy}) are excluded —
 * they are server-side concerns not needed by the frontend.
 *
 * @param id          the profile's application-assigned ID
 * @param userId      the owning user's application ID
 * @param username    the user-chosen handle
 * @param displayName optional display name; may be null
 * @param gender      optional gender; may be null
 * @param birthdate   optional birthdate; may be null
 * @param heightIn    optional height in inches; may be null
 * @param createdAt   the timestamp when the profile was created (UTC)
 */
public record UserProfileResponse(
        Long id,
        Long userId,
        String username,
        String displayName,
        String gender,
        LocalDate birthdate,
        Double heightIn,
        Instant createdAt
) {

    /**
     * Converts a {@link UserProfile} domain object to a {@code UserProfileResponse}.
     *
     * @param profile the domain profile; must not be null
     * @return a new {@code UserProfileResponse}
     */
    public static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(
                profile.id(),
                profile.userId(),
                profile.username(),
                profile.displayName(),
                profile.gender(),
                profile.birthdate(),
                profile.heightIn(),
                profile.createdAt()
        );
    }
}
