package com.liftit.muscle.persistence;

import com.liftit.muscle.Muscle;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link MuscleJpaEntity} domain conversion.
 *
 * <p>Verifies that {@code toDomain()} and {@code fromDomain()} correctly
 * round-trip all fields between the JPA entity and the {@link Muscle} domain record.
 */
class MuscleJpaEntityTest {

    private static final Long ID = 1L;
    private static final String NAME = "Quadriceps";
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Test
    void shouldConvertFromDomainPreservingAllFields() {
        // Given
        Muscle muscle = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When
        MuscleJpaEntity entity = MuscleJpaEntity.fromDomain(muscle);

        // Then
        Muscle result = entity.toDomain();
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(NOW, result.createdAt());
        assertEquals(SYSTEM_USER_ID, result.createdBy());
        assertEquals(NOW, result.updatedAt());
        assertEquals(SYSTEM_USER_ID, result.updatedBy());
    }

    @Test
    void shouldConvertToDomainPreservingAllFields() {
        // Given
        Muscle original = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        MuscleJpaEntity entity = MuscleJpaEntity.fromDomain(original);

        // When
        Muscle domain = entity.toDomain();

        // Then
        assertEquals(ID, domain.id());
        assertEquals(NAME, domain.name());
        assertEquals(NOW, domain.createdAt());
        assertEquals(SYSTEM_USER_ID, domain.createdBy());
        assertEquals(NOW, domain.updatedAt());
        assertEquals(SYSTEM_USER_ID, domain.updatedBy());
    }

    @Test
    void shouldReturnNonNullDomainFromRoundTrip() {
        // Given
        Muscle muscle = new Muscle(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When
        Muscle result = MuscleJpaEntity.fromDomain(muscle).toDomain();

        // Then
        assertNotNull(result);
    }

    @Test
    void shouldPreserveHardcodedIdOnRoundTrip() {
        // Given — muscle IDs are hardcoded, never DB-generated
        Muscle muscle = new Muscle(42L, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When
        Muscle result = MuscleJpaEntity.fromDomain(muscle).toDomain();

        // Then
        assertEquals(42L, result.id());
    }
}
