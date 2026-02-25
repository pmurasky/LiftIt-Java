package com.liftit.muscle;

import java.time.Instant;

/**
 * Domain model representing a muscle.
 *
 * <p>This entity maps to the {@code muscles} reference table. Muscles are
 * seeded with hardcoded IDs and used to classify exercises by the muscle
 * groups they target.
 *
 * <p>All fields include standard audit columns ({@code created_at},
 * {@code created_by}, {@code updated_at}, {@code updated_by}) as required
 * by the architecture. The {@code createdBy} and {@code updatedBy} fields
 * reference {@code users.id}; the system user (id=1) is used for seed rows.
 */
public record Muscle(
        Long id,
        String name,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy
) {
    /**
     * Compact canonical constructor — validates all required fields.
     *
     * @throws IllegalArgumentException if any field is null, or if name is blank
     */
    public Muscle {
        requireNonNull(id, "id");
        requireNonBlank(name, "name");
        requireNonNull(createdAt, "createdAt");
        requireNonNull(createdBy, "createdBy");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(updatedBy, "updatedBy");
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("Muscle." + fieldName + " must not be null");
        }
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Muscle." + fieldName + " must not be null or blank");
        }
    }
}
