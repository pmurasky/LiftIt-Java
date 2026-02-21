package com.liftit.user.exception;

import com.liftit.user.Auth0Id;
import com.liftit.user.Email;

/**
 * Thrown when an attempt is made to provision a user that already exists,
 * identified by either their Auth0 subject identifier or email address.
 */
public class DuplicateUserException extends RuntimeException {

    private DuplicateUserException(String message) {
        super(message);
    }

    /**
     * Creates an exception indicating a user with the given Auth0 ID already exists.
     *
     * @param auth0Id the duplicate Auth0 subject identifier
     * @return a new {@code DuplicateUserException}
     */
    public static DuplicateUserException forAuth0Id(Auth0Id auth0Id) {
        return new DuplicateUserException(
                "User already exists with auth0Id: " + auth0Id.value()
        );
    }

    /**
     * Creates an exception indicating a user with the given email already exists.
     *
     * @param email the duplicate email address
     * @return a new {@code DuplicateUserException}
     */
    public static DuplicateUserException forEmail(Email email) {
        return new DuplicateUserException(
                "User already exists with email: " + email.value()
        );
    }
}
