package com.liftit.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    private static final Long SYSTEM_ADMIN_ID = 1L;
    private static final Long USER_ID = 100L;
    private static final Long PROFILE_ID = 1L;
    private static final Instant NOW = Instant.now();

    @Test
    void shouldCreateUserProfileWithRequiredFieldsOnly() {
        // Given
        String username = "alice";

        // When
        UserProfile profile = new UserProfile(
                PROFILE_ID, USER_ID, username, null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        );

        // Then
        assertEquals(PROFILE_ID, profile.id());
        assertEquals(USER_ID, profile.userId());
        assertEquals(username, profile.username());
        assertNull(profile.displayName());
        assertNull(profile.gender());
        assertNull(profile.birthdate());
        assertNull(profile.heightIn());
        assertEquals(NOW, profile.createdAt());
        assertEquals(SYSTEM_ADMIN_ID, profile.createdBy());
        assertEquals(NOW, profile.updatedAt());
        assertEquals(SYSTEM_ADMIN_ID, profile.updatedBy());
    }

    @Test
    void shouldCreateUserProfileWithAllFields() {
        // Given
        String username = "bob";
        String displayName = "Bob Smith";
        String gender = "male";
        LocalDate birthdate = LocalDate.of(1990, 6, 15);
        Double heightIn = 70.0;

        // When
        UserProfile profile = new UserProfile(
                PROFILE_ID, USER_ID, username, displayName, gender, birthdate, heightIn,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        );

        // Then
        assertEquals(username, profile.username());
        assertEquals(displayName, profile.displayName());
        assertEquals(gender, profile.gender());
        assertEquals(birthdate, profile.birthdate());
        assertEquals(heightIn, profile.heightIn());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                null, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenUserIdIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, null, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenUsernameIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, USER_ID, null, null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenUsernameIsBlank() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, USER_ID, "  ", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenCreatedAtIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                null, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenCreatedByIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                NOW, null, NOW, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenUpdatedAtIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, null, SYSTEM_ADMIN_ID
        ));
    }

    @Test
    void shouldThrowWhenUpdatedByIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, null
        ));
    }

    @Test
    void shouldConsiderEqualWhenSameId() {
        // Given
        UserProfile a = new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        );
        UserProfile b = new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        );

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentIds() {
        // Given
        UserProfile a = new UserProfile(
                PROFILE_ID, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        );
        UserProfile b = new UserProfile(
                2L, USER_ID, "alice", null, null, null, null,
                NOW, SYSTEM_ADMIN_ID, NOW, SYSTEM_ADMIN_ID
        );

        // When / Then
        assertNotEquals(a, b);
    }
}
