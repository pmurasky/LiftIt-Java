package com.liftit.user;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Domain model representing a user's lifting profile.
 *
 * <p>Stores lifting-specific profile data separate from the {@code users} auth
 * identity table. Has a 1-to-1 relationship with {@code users} via {@code userId}.
 *
 * <p>Optional fields ({@code displayName}, {@code gender}, {@code birthdate},
 * {@code heightCm}) may be null — they are supplied during onboarding at the
 * user's discretion.
 *
 * <p>Body weight is intentionally absent here. Weight history is tracked as a
 * time-series in the {@code body_weight_history} table.
 */
public record UserProfile(
        Long id,
        Long userId,
        String username,
        String displayName,
        String gender,
        LocalDate birthdate,
        Double heightCm,
        String unitsPreference,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy
) {
    /**
     * Compact canonical constructor — validates required fields.
     * Optional fields (displayName, gender, birthdate, heightCm) may be null.
     *
     * @throws IllegalArgumentException if any required field is null or blank
     */
    public UserProfile {
        requireNonNull(id, "id");
        requireNonNull(userId, "userId");
        requireNonBlank(username, "username");
        requireNonNull(unitsPreference, "unitsPreference");
        requireNonNull(createdAt, "createdAt");
        requireNonNull(createdBy, "createdBy");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(updatedBy, "updatedBy");
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("UserProfile." + fieldName + " must not be null");
        }
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserProfile." + fieldName + " must not be null or blank");
        }
    }
}
