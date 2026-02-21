package com.liftit.user;

import java.util.Optional;

/**
 * Contract for managing user lifting profiles.
 *
 * <p>A profile is created during onboarding and has a 1-to-1 relationship
 * with a {@link User}. Callers depend on this abstraction rather than any
 * concrete implementation (Dependency Inversion Principle).
 */
public interface UserProfileService {

    /**
     * Creates a new profile for the given user.
     *
     * <p>The user identity is resolved server-side — callers pass the
     * internal {@code userId} extracted from the validated JWT, never
     * a caller-supplied value, to prevent IDOR.
     *
     * @param userId  the owning user's application ID; must not be null
     * @param request the profile fields supplied by the user; must not be null
     * @return the newly created {@link UserProfile}
     * @throws DuplicateProfileException if the user already has a profile,
     *                                   or if the chosen username is already taken
     * @throws IllegalArgumentException  if userId or request is null,
     *                                   or if required fields are missing
     */
    UserProfile createProfile(Long userId, CreateUserProfileRequest request);

    /**
     * Retrieves the profile for the given user.
     *
     * @param userId the owning user's application ID; must not be null
     * @return an {@link Optional} containing the profile, or empty if not found
     */
    Optional<UserProfile> getProfile(Long userId);
}
