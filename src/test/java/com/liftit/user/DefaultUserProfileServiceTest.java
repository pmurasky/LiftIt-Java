package com.liftit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultUserProfileServiceTest {

    private static final Long USER_ID = 100L;
    private static final Long PROFILE_ID = 1L;

    private UserProfileRepository profileRepository;
    private UserProfileService service;

    @BeforeEach
    void setUp() {
        profileRepository = mock(UserProfileRepository.class);
        service = new DefaultUserProfileService(profileRepository);
    }

    // --- createProfile ---

    @Test
    void shouldCreateProfileWhenUserHasNoExistingProfileAndUsernameIsAvailable() {
        // Given
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "alice_lifts", "Alice Smith", "female",
                LocalDate.of(1990, 6, 15), 165.0, "metric"
        );
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.findByUsername("alice_lifts")).thenReturn(Optional.empty());
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        UserProfile result = service.createProfile(USER_ID, request);

        // Then
        assertNotNull(result);
        assertEquals(USER_ID, result.userId());
        assertEquals("alice_lifts", result.username());
        assertEquals("Alice Smith", result.displayName());
        assertEquals("female", result.gender());
        assertEquals(LocalDate.of(1990, 6, 15), result.birthdate());
        assertEquals(165.0, result.heightCm());
        assertEquals("metric", result.unitsPreference());
        verify(profileRepository).save(any(UserProfile.class));
    }

    @Test
    void shouldCreateProfileWithRequiredFieldsOnlyWhenOptionalFieldsAreNull() {
        // Given
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "bob_lifts", null, null, null, null, "imperial"
        );
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.findByUsername("bob_lifts")).thenReturn(Optional.empty());
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        UserProfile result = service.createProfile(USER_ID, request);

        // Then
        assertEquals("bob_lifts", result.username());
        assertNull(result.displayName());
        assertNull(result.gender());
        assertNull(result.birthdate());
        assertNull(result.heightCm());
        assertEquals("imperial", result.unitsPreference());
    }

    @Test
    void shouldSetSystemAdminAsAuditUserOnProfileCreation() {
        // Given
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "carol_lifts", null, null, null, null, "metric"
        );
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.findByUsername("carol_lifts")).thenReturn(Optional.empty());
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        UserProfile result = service.createProfile(USER_ID, request);

        // Then — system admin (id=1) is both createdBy and updatedBy per architecture
        assertEquals(1L, result.createdBy());
        assertEquals(1L, result.updatedBy());
    }

    @Test
    void shouldSetCreatedAtAndUpdatedAtToCurrentTimeOnProfileCreation() {
        // Given
        Instant before = Instant.now();
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "dave_lifts", null, null, null, null, "metric"
        );
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.findByUsername("dave_lifts")).thenReturn(Optional.empty());
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        UserProfile result = service.createProfile(USER_ID, request);
        Instant after = Instant.now();

        // Then
        assertFalse(result.createdAt().isBefore(before));
        assertFalse(result.createdAt().isAfter(after));
        assertFalse(result.updatedAt().isBefore(before));
        assertFalse(result.updatedAt().isAfter(after));
    }

    @Test
    void shouldThrowDuplicateProfileExceptionWhenUserAlreadyHasProfile() {
        // Given
        UserProfile existing = existingProfile(USER_ID, "alice_lifts");
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "alice_new", null, null, null, null, "metric"
        );

        // When / Then
        DuplicateProfileException ex = assertThrows(
                DuplicateProfileException.class,
                () -> service.createProfile(USER_ID, request)
        );
        assertTrue(ex.getMessage().contains("100"));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateProfileExceptionWhenUsernameAlreadyTaken() {
        // Given
        UserProfile existing = existingProfile(999L, "taken_name");
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.findByUsername("taken_name")).thenReturn(Optional.of(existing));
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "taken_name", null, null, null, null, "metric"
        );

        // When / Then
        DuplicateProfileException ex = assertThrows(
                DuplicateProfileException.class,
                () -> service.createProfile(USER_ID, request)
        );
        assertTrue(ex.getMessage().contains("taken_name"));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserIdIsNull() {
        // Given
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "user", null, null, null, null, "metric"
        );

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.createProfile(null, request));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRequestIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.createProfile(USER_ID, null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUsernameIsBlank() {
        // Given
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "  ", null, null, null, null, "metric"
        );

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.createProfile(USER_ID, request));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUnitsPreferenceIsNull() {
        // Given
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "user", null, null, null, null, null
        );

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.createProfile(USER_ID, request));
    }

    // --- getProfile ---

    @Test
    void shouldReturnProfileWhenProfileExistsForUser() {
        // Given
        UserProfile profile = existingProfile(USER_ID, "alice_lifts");
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));

        // When
        Optional<UserProfile> result = service.getProfile(USER_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }

    @Test
    void shouldReturnEmptyWhenNoProfileExistsForUser() {
        // Given
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // When
        Optional<UserProfile> result = service.getProfile(USER_ID);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGetProfileUserIdIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.getProfile(null));
    }

    // --- helpers ---

    private UserProfile existingProfile(Long userId, String username) {
        return new UserProfile(
                PROFILE_ID, userId, username, null, null, null, null, "metric",
                Instant.now(), 1L, Instant.now(), 1L
        );
    }
}
