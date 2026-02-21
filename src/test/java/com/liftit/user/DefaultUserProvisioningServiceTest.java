package com.liftit.user;

import com.liftit.user.exception.DuplicateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultUserProvisioningServiceTest {

    private UserRepository userRepository;
    private UserProvisioningService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new DefaultUserProvisioningService(userRepository);
    }

    @Test
    void shouldProvisionNewUserWhenAuth0IdAndEmailAreUnique() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|newuser123");
        Email email = Email.of("newuser@example.com");
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        User result = service.provision(auth0Id, email);

        // Then
        assertNotNull(result);
        assertEquals(auth0Id, result.auth0Id());
        assertEquals(email, result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldSetSystemAdminAsAuditUserOnProvisioning() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|audituser");
        Email email = Email.of("audit@example.com");
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        User result = service.provision(auth0Id, email);

        // Then — system admin (id=1) is both createdBy and updatedBy per architecture
        assertEquals(1L, result.createdBy());
        assertEquals(1L, result.updatedBy());
    }

    @Test
    void shouldUseZeroIdSentinelSoRepositoryAssignsDatabaseIdentity() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|nullid");
        Email email = Email.of("nullid@example.com");
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When — id=0L is sentinel value; DB assigns the real BIGINT IDENTITY
        User result = service.provision(auth0Id, email);

        // Then
        assertEquals(0L, result.id());
    }

    @Test
    void shouldThrowDuplicateUserExceptionWhenAuth0IdAlreadyExists() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|existing");
        Email email = Email.of("existing@example.com");
        User existing = existingUser(auth0Id, email);
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(existing));

        // When / Then
        DuplicateUserException ex = assertThrows(
                DuplicateUserException.class,
                () -> service.provision(auth0Id, email)
        );
        assertTrue(ex.getMessage().contains("auth0|existing"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateUserExceptionWhenEmailAlreadyExists() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|brandnew");
        Email email = Email.of("taken@example.com");
        User existing = existingUser(Auth0Id.of("auth0|other"), email);
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existing));

        // When / Then
        DuplicateUserException ex = assertThrows(
                DuplicateUserException.class,
                () -> service.provision(auth0Id, email)
        );
        assertTrue(ex.getMessage().contains("taken@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAuth0IdIsNull() {
        // Given / When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.provision(null, Email.of("x@example.com"))
        );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsNull() {
        // Given / When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.provision(Auth0Id.of("auth0|x"), null)
        );
    }

    @Test
    void shouldSetCreatedAtAndUpdatedAtToCurrentTime() {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|timestamps");
        Email email = Email.of("ts@example.com");
        Instant before = Instant.now();
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        User result = service.provision(auth0Id, email);
        Instant after = Instant.now();

        // Then
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertFalse(result.createdAt().isBefore(before));
        assertFalse(result.createdAt().isAfter(after));
    }

    // --- helpers ---

    private User existingUser(Auth0Id auth0Id, Email email) {
        return new User(99L, auth0Id, email, Instant.now(), 1L, Instant.now(), 1L);
    }
}
