package com.liftit.user;

import java.time.Instant;

/**
 * Domain model representing an application user.
 *
 * <p>This entity maps to the {@code users} table and stores only the data
 * required to link an Auth0 identity to application data. Auth0 owns all
 * credentials — passwords are never stored here.
 *
 * <p>All fields include standard audit columns ({@code created_at},
 * {@code created_by}, {@code updated_at}, {@code updated_by}) as required
 * by the architecture. The {@code createdBy} and {@code updatedBy} fields
 * reference {@code users.id}; the system admin (id=1) is used for seed rows.
 */
public record User(
        Long id,
        Auth0Id auth0Id,
        Email email,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy
) {
    /**
     * Compact canonical constructor — validates all required fields.
     *
     * @throws IllegalArgumentException if any field is null
     */
    public User {
        requireNonNull(id, "id");
        requireNonNull(auth0Id, "auth0Id");
        requireNonNull(email, "email");
        requireNonNull(createdAt, "createdAt");
        requireNonNull(createdBy, "createdBy");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(updatedBy, "updatedBy");
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("User." + fieldName + " must not be null");
        }
    }
}
