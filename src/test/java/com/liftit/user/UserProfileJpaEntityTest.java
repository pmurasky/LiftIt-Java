package com.liftit.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link UserProfileJpaEntity} domain conversion.
 *
 * <p>Verifies that {@code toDomain()} and {@code fromDomain()} correctly
 * round-trip all fields between the JPA entity and the {@link UserProfile} domain record.
 */
class UserProfileJpaEntityTest {

    private static final Long ID = 7L;
    private static final Long USER_ID = 42L;
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long AUDIT_USER = 1L;

    @Test
    void shouldConvertFromDomainPreservingAllFields() {
        // Given
        UserProfile profile = new UserProfile(ID, USER_ID, "lifter99", "Test User",
                "male", LocalDate.of(1990, 5, 15), 180.0, "metric",
                NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        UserProfileJpaEntity entity = UserProfileJpaEntity.fromDomain(profile);
        UserProfile result = entity.toDomain();

        // Then
        assertEquals(ID, result.id());
        assertEquals(USER_ID, result.userId());
        assertEquals("lifter99", result.username());
        assertEquals("Test User", result.displayName());
        assertEquals("male", result.gender());
        assertEquals(LocalDate.of(1990, 5, 15), result.birthdate());
        assertEquals(180.0, result.heightCm());
        assertEquals("metric", result.unitsPreference());
        assertEquals(NOW, result.createdAt());
        assertEquals(AUDIT_USER, result.createdBy());
        assertEquals(NOW, result.updatedAt());
        assertEquals(AUDIT_USER, result.updatedBy());
    }

    @Test
    void shouldMapIdToNullWhenDomainIdIsZero() {
        // Given — id=0 signals "new entity, let the DB assign the PK"
        UserProfile profile = new UserProfile(0L, USER_ID, "newlifter", null, null, null,
                null, "imperial", NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        UserProfileJpaEntity entity = UserProfileJpaEntity.fromDomain(profile);

        // Then — null id lets @GeneratedValue assign the real PK on INSERT
        assertNull(entity.getId());
    }

    @Test
    void shouldPreserveNullOptionalFields() {
        // Given — optional fields (displayName, gender, birthdate, heightCm) may be null
        UserProfile profile = new UserProfile(ID, USER_ID, "minimallifter", null, null, null,
                null, "metric", NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        UserProfile result = UserProfileJpaEntity.fromDomain(profile).toDomain();

        // Then
        assertNull(result.displayName());
        assertNull(result.gender());
        assertNull(result.birthdate());
        assertNull(result.heightCm());
    }

    @Test
    void shouldReturnNonNullDomainFromRoundTrip() {
        // Given
        UserProfile profile = new UserProfile(ID, USER_ID, "lifter99", null, null, null,
                null, "metric", NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        UserProfile result = UserProfileJpaEntity.fromDomain(profile).toDomain();

        // Then
        assertNotNull(result);
    }
}
