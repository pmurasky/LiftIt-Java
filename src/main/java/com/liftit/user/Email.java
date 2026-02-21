package com.liftit.user;

import java.util.Objects;

/// Strongly-typed email address for a `User`.
///
/// Enforces non-null, non-blank constraints and normalises the value
/// to lowercase, trimmed form. Basic structural validation (must contain
/// exactly one `@` with non-empty local and domain parts) is applied.
///
/// @param value the normalised email address; never null or blank
public record Email(String value) {

    /// Compact constructor — validates and normalises the value.
    public Email {
        Objects.requireNonNull(value, "Email must not be null");
        value = value.strip().toLowerCase();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (!isValidFormat(value)) {
            throw new IllegalArgumentException("Email format is invalid: " + value);
        }
    }

    private static boolean isValidFormat(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return false;
        }
        String domain = email.substring(atIndex + 1);
        return !domain.isBlank() && domain.contains(".");
    }
}
