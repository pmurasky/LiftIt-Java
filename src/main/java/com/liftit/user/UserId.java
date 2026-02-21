package com.liftit.user;

import java.util.Objects;
import java.util.UUID;

/// Strongly-typed identity for a `User`.
///
/// Wraps a `UUID` to prevent primitive obsession and ensure
/// user IDs cannot be confused with other UUID-based identifiers.
///
/// @param value the underlying UUID; must not be null
public record UserId(UUID value) {

    /// Compact constructor — validates the value on construction.
    public UserId {
        Objects.requireNonNull(value, "UserId value must not be null");
    }

    /// Creates a new random `UserId`.
    ///
    /// @return a new `UserId` backed by a random UUID
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /// Parses a `UserId` from a UUID string.
    ///
    /// @param value the UUID string; must not be null
    /// @return a `UserId` wrapping the parsed UUID
    /// @throws IllegalArgumentException if the string is not a valid UUID
    public static UserId of(String value) {
        Objects.requireNonNull(value, "UserId value must not be null");
        return new UserId(UUID.fromString(value));
    }
}
