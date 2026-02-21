package com.liftit.user;

import java.util.Optional;

/**
 * Data-access contract for {@link User} entities.
 *
 * <p>Callers depend on this abstraction rather than any concrete implementation
 * (Dependency Inversion Principle). Implementations may store users in a relational
 * database, in-memory store, or any other backing mechanism.
 */
public interface UserRepository {

    /**
     * Persists a user. If a user with the same {@code id} already exists, it is replaced.
     *
     * @param user the user to save; must not be null
     * @return the saved user
     */
    User save(User user);

    /**
     * Finds a user by their internal application ID.
     *
     * @param id the user's application ID; must not be null
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by their Auth0 subject identifier.
     *
     * @param auth0Id the Auth0 sub claim; must not be null
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findByAuth0Id(Auth0Id auth0Id);

    /**
     * Finds a user by their email address.
     *
     * @param email the normalised email address; must not be null
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findByEmail(Email email);

    /**
     * Deletes the user with the given ID. If no such user exists, this is a no-op.
     *
     * @param id the ID of the user to delete; must not be null
     */
    void delete(Long id);
}
