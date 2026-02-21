package com.liftit.user;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Default implementation of {@link UserProfileService}.
 *
 * <p>Enforces the 1-to-1 user↔profile constraint and the global username
 * uniqueness constraint before delegating persistence to the repository.
 * The system admin user ({@code id = 1}) is used for {@code created_by}
 * and {@code updated_by} on creation, as required by the architecture.
 * The database assigns the real {@code BIGINT IDENTITY} primary key.
 */
@Service
public class DefaultUserProfileService implements UserProfileService {

    private static final long SYSTEM_ADMIN_ID = 1L;
    private static final long UNASSIGNED_ID = 0L;

    private final UserProfileRepository profileRepository;

    public DefaultUserProfileService(UserProfileRepository profileRepository) {
        if (profileRepository == null) {
            throw new IllegalArgumentException("profileRepository must not be null");
        }
        this.profileRepository = profileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile createProfile(Long userId, CreateUserProfileRequest request) {
        validateInputs(userId, request);
        checkForDuplicates(userId, request.username());
        return profileRepository.save(buildProfile(userId, request));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> getProfile(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        return profileRepository.findByUserId(userId);
    }

    private void validateInputs(Long userId, CreateUserProfileRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (request.unitsPreference() == null) {
            throw new IllegalArgumentException("unitsPreference must not be null");
        }
    }

    private void checkForDuplicates(Long userId, String username) {
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw DuplicateProfileException.forUser(userId);
        }
        if (profileRepository.findByUsername(username).isPresent()) {
            throw DuplicateProfileException.forUsername(username);
        }
    }

    private UserProfile buildProfile(Long userId, CreateUserProfileRequest request) {
        Instant now = Instant.now();
        return new UserProfile(
                UNASSIGNED_ID, userId,
                request.username(), request.displayName(),
                request.gender(), request.birthdate(),
                request.heightCm(), request.unitsPreference(),
                now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID
        );
    }
}
