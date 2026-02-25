package com.liftit.exercise.persistence;

import com.liftit.exercise.ExerciseCategory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link ExerciseCategoryJpaEntity} domain conversion.
 *
 * <p>Verifies that {@code toDomain()} and {@code fromDomain()} correctly
 * round-trip all fields between the JPA entity and the {@link ExerciseCategory} domain record.
 */
class ExerciseCategoryJpaEntityTest {

    private static final Long ID = 1L;
    private static final String NAME = "Strength";
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Test
    void shouldConvertFromDomainPreservingAllFields() {
        // Given
        ExerciseCategory category = new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When
        ExerciseCategoryJpaEntity entity = ExerciseCategoryJpaEntity.fromDomain(category);

        // Then
        ExerciseCategory result = entity.toDomain();
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
        ExerciseCategory original = new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        ExerciseCategoryJpaEntity entity = ExerciseCategoryJpaEntity.fromDomain(original);

        // When
        ExerciseCategory domain = entity.toDomain();

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
        ExerciseCategory category = new ExerciseCategory(ID, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When
        ExerciseCategory result = ExerciseCategoryJpaEntity.fromDomain(category).toDomain();

        // Then
        assertNotNull(result);
    }

    @Test
    void shouldPreserveHardcodedIdOnRoundTrip() {
        // Given — category IDs are hardcoded, never DB-generated
        ExerciseCategory category = new ExerciseCategory(1L, NAME, NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);

        // When
        ExerciseCategory result = ExerciseCategoryJpaEntity.fromDomain(category).toDomain();

        // Then
        assertEquals(1L, result.id());
    }
}
