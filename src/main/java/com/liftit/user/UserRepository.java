package com.liftit.user;

import java.util.Optional;

/// Data-access contract for `User` persistence.
///
/// Implementations must be consistent: a `User` saved via `save()` must
/// be retrievable by its `id`, `username`, and `email`. All query methods
/// return `Optional` to make the absence of a result explicit at the call
/// site, eliminating null-check ambiguity.
///
/// Implementations are responsible for enforcing uniqueness constraints on
/// `username` and `email`.
public interface UserRepository {

    /// Persists a `User`, creating or replacing the record for its `id`.
    ///
    /// @param user the user to persist; must not be null
    /// @return the saved user (may be the same instance or a new one)
    /// @throws IllegalArgumentException if `username` or `email` is already
    ///         taken by a different user
    User save(User user);

    /// Looks up a `User` by its unique identifier.
    ///
    /// @param id the identifier to search for; must not be null
    /// @return an `Optional` containing the user, or empty if not found
    Optional<User> findById(UserId id);

    /// Looks up a `User` by username.
    ///
    /// @param username the username to search for; must not be null
    /// @return an `Optional` containing the user, or empty if not found
    Optional<User> findByUsername(Username username);

    /// Looks up a `User` by email address.
    ///
    /// @param email the email address to search for; must not be null
    /// @return an `Optional` containing the user, or empty if not found
    Optional<User> findByEmail(Email email);

    /// Removes the `User` with the given `id`.
    ///
    /// Is a no-op if no user with that `id` exists.
    ///
    /// @param id the identifier of the user to remove; must not be null
    void delete(UserId id);
}
