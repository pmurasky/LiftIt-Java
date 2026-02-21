package com.liftit.user;

import java.util.Objects;

/// Strongly-typed username for a `User`.
///
/// Enforces non-null, non-blank constraints and normalises the value
/// by trimming surrounding whitespace. The database schema limits
/// usernames to 20 characters; this object enforces that constraint
/// at the domain level.
///
/// @param value the trimmed username; never null or blank, max 20 characters
public record Username(String value) {

    private static final int MAX_LENGTH = 20;

    /// Compact constructor — validates and normalises the value.
    public Username {
        Objects.requireNonNull(value, "Username must not be null");
        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Username must not exceed %d characters".formatted(MAX_LENGTH));
        }
    }
}
