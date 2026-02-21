package com.liftit.user;

import java.time.Instant;
import java.util.Objects;

/// Immutable domain entity representing a registered user.
///
/// All fields are strongly typed via value objects to eliminate primitive
/// obsession. Creation timestamps are recorded in UTC.
///
/// @param id        the unique identifier; must not be null
/// @param username  the user's unique display name; must not be null
/// @param email     the user's unique email address; must not be null
/// @param createdAt the UTC instant when the user was created; must not be null
public record User(UserId id, Username username, Email email, Instant createdAt) {

    /// Compact constructor — validates all fields.
    public User {
        Objects.requireNonNull(id, "User id must not be null");
        Objects.requireNonNull(username, "User username must not be null");
        Objects.requireNonNull(email, "User email must not be null");
        Objects.requireNonNull(createdAt, "User createdAt must not be null");
    }

    /// Creates a new `User` with a generated ID and the current UTC timestamp.
    ///
    /// @param username the user's unique display name; must not be null
    /// @param email    the user's unique email address; must not be null
    /// @return a new `User` instance ready to be persisted
    public static User create(Username username, Email email) {
        return new User(UserId.generate(), username, email, Instant.now());
    }
}
