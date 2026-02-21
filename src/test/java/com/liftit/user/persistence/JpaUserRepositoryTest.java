package com.liftit.user.persistence;

import com.liftit.user.Auth0Id;
import com.liftit.user.Email;
import com.liftit.user.User;
import com.liftit.user.UserRepository;
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
 * Unit tests for {@link JpaUserRepository}.
 *
 * <p>Verifies that each domain operation on {@link UserRepository} correctly
 * delegates to the Spring Data {@link UserJpaRepository} and that entity-to-domain
 * conversion is applied before returning results.
 */
@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryTest {

    @Mock
    private UserJpaRepository springDataRepository;

    private JpaUserRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long USER_ID = 42L;
    private static final Auth0Id AUTH0_ID = Auth0Id.of("auth0|testuser");
    private static final Email EMAIL = Email.of("test@example.com");

    @BeforeEach
    void setUp() {
        repository = new JpaUserRepository(springDataRepository);
    }

    @Test
    void shouldSaveUserAndReturnSavedDomain() {
        // Given
        User user = new User(0L, AUTH0_ID, EMAIL, NOW, 1L, NOW, 1L);
        UserJpaEntity savedEntity = UserJpaEntity.fromDomain(
                new User(USER_ID, AUTH0_ID, EMAIL, NOW, 1L, NOW, 1L));
        when(springDataRepository.save(any(UserJpaEntity.class))).thenReturn(savedEntity);

        // When
        User result = repository.save(user);

        // Then
        assertEquals(USER_ID, result.id());
        assertEquals(AUTH0_ID, result.auth0Id());
        assertEquals(EMAIL, result.email());
        verify(springDataRepository).save(any(UserJpaEntity.class));
    }

    @Test
    void shouldFindUserByIdWhenExists() {
        // Given
        User user = new User(USER_ID, AUTH0_ID, EMAIL, NOW, 1L, NOW, 1L);
        when(springDataRepository.findById(USER_ID))
                .thenReturn(Optional.of(UserJpaEntity.fromDomain(user)));

        // When
        Optional<User> result = repository.findById(USER_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().id());
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        // Given
        when(springDataRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // When
        Optional<User> result = repository.findById(USER_ID);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindUserByAuth0IdWhenExists() {
        // Given
        User user = new User(USER_ID, AUTH0_ID, EMAIL, NOW, 1L, NOW, 1L);
        when(springDataRepository.findByAuth0Id(AUTH0_ID.value()))
                .thenReturn(Optional.of(UserJpaEntity.fromDomain(user)));

        // When
        Optional<User> result = repository.findByAuth0Id(AUTH0_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(AUTH0_ID, result.get().auth0Id());
    }

    @Test
    void shouldReturnEmptyWhenFindByAuth0IdNotFound() {
        // Given
        when(springDataRepository.findByAuth0Id(AUTH0_ID.value())).thenReturn(Optional.empty());

        // When
        Optional<User> result = repository.findByAuth0Id(AUTH0_ID);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindUserByEmailWhenExists() {
        // Given
        User user = new User(USER_ID, AUTH0_ID, EMAIL, NOW, 1L, NOW, 1L);
        when(springDataRepository.findByEmail(EMAIL.value()))
                .thenReturn(Optional.of(UserJpaEntity.fromDomain(user)));

        // When
        Optional<User> result = repository.findByEmail(EMAIL);

        // Then
        assertTrue(result.isPresent());
        assertEquals(EMAIL, result.get().email());
    }

    @Test
    void shouldReturnEmptyWhenFindByEmailNotFound() {
        // Given
        when(springDataRepository.findByEmail(EMAIL.value())).thenReturn(Optional.empty());

        // When
        Optional<User> result = repository.findByEmail(EMAIL);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteUserById() {
        // Given — deleteById is a void Spring Data method

        // When
        repository.delete(USER_ID);

        // Then
        verify(springDataRepository).deleteById(USER_ID);
    }
}
