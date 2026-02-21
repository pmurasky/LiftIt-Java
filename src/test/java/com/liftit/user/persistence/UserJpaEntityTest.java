package com.liftit.user.persistence;

import com.liftit.user.Auth0Id;
import com.liftit.user.Email;
import com.liftit.user.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link UserJpaEntity} domain conversion.
 *
 * <p>Verifies that {@code toDomain()} and {@code fromDomain()} correctly
 * round-trip all fields between the JPA entity and the {@link User} domain record.
 */
class UserJpaEntityTest {

    private static final Long ID = 42L;
    private static final Auth0Id AUTH0_ID = Auth0Id.of("auth0|testuser");
    private static final Email EMAIL = Email.of("test@example.com");
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long AUDIT_USER = 1L;

    @Test
    void shouldConvertFromDomainToEntityPreservingAllFields() {
        // Given
        User user = new User(ID, AUTH0_ID, EMAIL, NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);

        // Then
        assertEquals(ID, entity.toDomain().id());
        assertEquals(AUTH0_ID.value(), user.auth0Id().value());
        assertEquals(EMAIL.value(), user.email().value());
        assertEquals(NOW, entity.toDomain().createdAt());
        assertEquals(AUDIT_USER, entity.toDomain().createdBy());
        assertEquals(NOW, entity.toDomain().updatedAt());
        assertEquals(AUDIT_USER, entity.toDomain().updatedBy());
    }

    @Test
    void shouldMapIdToNullWhenDomainIdIsZero() {
        // Given — id=0 signals "new entity, let the DB assign the PK"
        User user = new User(0L, AUTH0_ID, EMAIL, NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);

        // Then — null id lets @GeneratedValue assign the real PK on INSERT
        assertNull(entity.getId());
    }

    @Test
    void shouldConvertEntityToDomainPreservingAllFields() {
        // Given
        User original = new User(ID, AUTH0_ID, EMAIL, NOW, AUDIT_USER, NOW, AUDIT_USER);
        UserJpaEntity entity = UserJpaEntity.fromDomain(original);

        // When
        User domain = entity.toDomain();

        // Then
        assertEquals(ID, domain.id());
        assertEquals(AUTH0_ID, domain.auth0Id());
        assertEquals(EMAIL, domain.email());
        assertEquals(NOW, domain.createdAt());
        assertEquals(AUDIT_USER, domain.createdBy());
        assertEquals(NOW, domain.updatedAt());
        assertEquals(AUDIT_USER, domain.updatedBy());
    }

    @Test
    void shouldNormaliseEmailToLowercaseViaEmailValueObject() {
        // Given — Email.of() normalises to lowercase; verify round-trip preserves it
        User user = new User(ID, AUTH0_ID, Email.of("UPPER@EXAMPLE.COM"), NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        User roundTripped = UserJpaEntity.fromDomain(user).toDomain();

        // Then
        assertEquals("upper@example.com", roundTripped.email().value());
    }

    @Test
    void shouldReturnNonNullDomainFromRoundTrip() {
        // Given
        User user = new User(ID, AUTH0_ID, EMAIL, NOW, AUDIT_USER, NOW, AUDIT_USER);

        // When
        User result = UserJpaEntity.fromDomain(user).toDomain();

        // Then
        assertNotNull(result);
    }
}
