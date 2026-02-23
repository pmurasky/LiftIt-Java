package com.liftit.user.persistence;

import com.liftit.user.UserProfile;
import com.liftit.user.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JpaUserProfileRepository}.
 *
 * <p>Verifies that each domain operation on {@link UserProfileRepository} correctly
 * delegates to the Spring Data {@link UserProfileJpaRepository} and that entity-to-domain
 * conversion is applied before returning results.
 */
@ExtendWith(MockitoExtension.class)
class JpaUserProfileRepositoryTest {

    @Mock
    private UserProfileJpaRepository springDataRepository;

    private JpaUserProfileRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long PROFILE_ID = 7L;
    private static final Long USER_ID = 42L;

    @BeforeEach
    void setUp() {
        repository = new JpaUserProfileRepository(springDataRepository);
    }

    private UserProfile buildProfile(Long id) {
        return new UserProfile(id, USER_ID, "lifter99", null, null, null,
                null, NOW, 1L, NOW, 1L);
    }

    @Test
    void shouldSaveProfileAndReturnSavedDomain() {
        // Given
        UserProfile profile = buildProfile(0L);
        UserProfileJpaEntity savedEntity = UserProfileJpaEntity.fromDomain(buildProfile(PROFILE_ID));
        when(springDataRepository.save(any(UserProfileJpaEntity.class))).thenReturn(savedEntity);

        // When
        UserProfile result = repository.save(profile);

        // Then
        assertEquals(PROFILE_ID, result.id());
        assertEquals(USER_ID, result.userId());
        assertEquals("lifter99", result.username());
        verify(springDataRepository).save(any(UserProfileJpaEntity.class));
    }

    @Test
    void shouldFindProfileByUserIdWhenExists() {
        // Given
        when(springDataRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(UserProfileJpaEntity.fromDomain(buildProfile(PROFILE_ID))));

        // When
        Optional<UserProfile> result = repository.findByUserId(USER_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().userId());
    }

    @Test
    void shouldReturnEmptyWhenFindByUserIdNotFound() {
        // Given
        when(springDataRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // When
        Optional<UserProfile> result = repository.findByUserId(USER_ID);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindProfileByUsernameWhenExists() {
        // Given
        when(springDataRepository.findByUsername("lifter99"))
                .thenReturn(Optional.of(UserProfileJpaEntity.fromDomain(buildProfile(PROFILE_ID))));

        // When
        Optional<UserProfile> result = repository.findByUsername("lifter99");

        // Then
        assertTrue(result.isPresent());
        assertEquals("lifter99", result.get().username());
    }

    @Test
    void shouldReturnEmptyWhenFindByUsernameNotFound() {
        // Given
        when(springDataRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // When
        Optional<UserProfile> result = repository.findByUsername("unknown");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteProfileById() {
        // Given — deleteById is a void Spring Data method

        // When
        repository.delete(PROFILE_ID);

        // Then
        verify(springDataRepository).deleteById(PROFILE_ID);
    }
}
