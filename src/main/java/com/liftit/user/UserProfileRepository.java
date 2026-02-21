package com.liftit.user;

import java.util.Optional;

/**
 * Data-access contract for {@link UserProfile} entities.
 *
 * <p>Callers depend on this abstraction rather than any concrete implementation
 * (Dependency Inversion Principle). Implementations may store profiles in a
 * relational database, in-memory store, or any other backing mechanism.
 */
public interface UserProfileRepository {

    /**
     * Persists a user profile. If a profile with the same {@code id} already
     * exists, it is replaced.
     *
     * @param profile the profile to save; must not be null
     * @return the saved profile
     */
    UserProfile save(UserProfile profile);

    /**
     * Finds a profile by the owning user's application ID.
     * Each user has at most one profile (1-to-1 relationship).
     *
     * @param userId the owning user's application ID; must not be null
     * @return an {@link Optional} containing the profile, or empty if not found
     */
    Optional<UserProfile> findByUserId(Long userId);

    /**
     * Finds a profile by the chosen username.
     *
     * @param username the username to look up; must not be null or blank
     * @return an {@link Optional} containing the profile, or empty if not found
     */
    Optional<UserProfile> findByUsername(String username);

    /**
     * Deletes the profile with the given ID. If no such profile exists, this is a no-op.
     *
     * @param id the ID of the profile to delete; must not be null
     */
    void delete(Long id);
}
