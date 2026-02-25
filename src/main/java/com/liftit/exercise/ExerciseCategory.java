package com.liftit.exercise;

import java.time.Instant;

/**
 * Domain model representing an exercise category reference row.
 *
 * <p>Maps to the {@code exercise_categories} reference table. Categories are
 * system-defined and seeded via Liquibase. The corresponding Java enum is
 * {@link ExerciseCategoryEnum}, validated against this table at startup.
 *
 * <p>All fields include standard audit columns as required by the architecture.
 * The system user (id=1) owns all seed rows.
 */
public record ExerciseCategory(
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
    public ExerciseCategory {
        requireNonNull(id, "id");
        requireNonBlank(name, "name");
        requireNonNull(createdAt, "createdAt");
        requireNonNull(createdBy, "createdBy");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(updatedBy, "updatedBy");
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("ExerciseCategory." + fieldName + " must not be null");
        }
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "ExerciseCategory." + fieldName + " must not be null or blank");
        }
    }
}
