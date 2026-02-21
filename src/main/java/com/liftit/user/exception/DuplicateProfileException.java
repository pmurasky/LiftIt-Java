package com.liftit.user.exception;

/**
 * Thrown when an attempt is made to create a profile that conflicts with an
 * existing one — either the user already has a profile (1-to-1 constraint)
 * or the chosen username is already taken.
 */
public class DuplicateProfileException extends RuntimeException {

    private DuplicateProfileException(String message) {
        super(message);
    }

    /**
     * Creates an exception indicating the user already has a profile.
     *
     * @param userId the user whose profile already exists
     * @return a new {@code DuplicateProfileException}
     */
    public static DuplicateProfileException forUser(Long userId) {
        return new DuplicateProfileException(
                "Profile already exists for userId: " + userId
        );
    }

    /**
     * Creates an exception indicating the chosen username is already taken.
     *
     * @param username the duplicate username
     * @return a new {@code DuplicateProfileException}
     */
    public static DuplicateProfileException forUsername(String username) {
        return new DuplicateProfileException(
                "Profile already exists with username: " + username
        );
    }
}
