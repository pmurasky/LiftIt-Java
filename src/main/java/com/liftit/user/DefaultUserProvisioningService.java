package com.liftit.user;

import com.liftit.user.exception.DuplicateUserException;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Default implementation of {@link UserProvisioningService}.
 *
 * <p>On first login, Auth0 issues a JWT containing the user's {@code sub}
 * (auth0Id) and {@code email} claims. This service creates the corresponding
 * local {@code users} row so the application can associate data with the user.
 *
 * <p>The system admin user ({@code id = 1}) is used for {@code created_by}
 * and {@code updated_by} on first insert, as required by the architecture.
 * The database assigns the real {@code BIGINT IDENTITY} primary key.
 */
@Service
public class DefaultUserProvisioningService implements UserProvisioningService {

    private static final long SYSTEM_ADMIN_ID = 1L;
    private static final long UNASSIGNED_ID = 0L;

    private final UserRepository userRepository;

    public DefaultUserProvisioningService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository must not be null");
        }
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User provision(Auth0Id auth0Id, Email email) {
        validateInputs(auth0Id, email);
        checkForDuplicates(auth0Id, email);
        return userRepository.save(buildUser(auth0Id, email));
    }

    private void validateInputs(Auth0Id auth0Id, Email email) {
        if (auth0Id == null) {
            throw new IllegalArgumentException("auth0Id must not be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
    }

    private void checkForDuplicates(Auth0Id auth0Id, Email email) {
        if (userRepository.findByAuth0Id(auth0Id).isPresent()) {
            throw DuplicateUserException.forAuth0Id(auth0Id);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw DuplicateUserException.forEmail(email);
        }
    }

    private User buildUser(Auth0Id auth0Id, Email email) {
        Instant now = Instant.now();
        return new User(UNASSIGNED_ID, auth0Id, email, now, SYSTEM_ADMIN_ID, now, SYSTEM_ADMIN_ID);
    }
}
