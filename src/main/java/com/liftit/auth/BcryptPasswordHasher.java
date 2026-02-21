package com.liftit.auth;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * BCrypt-based implementation of {@link PasswordHasher}.
 *
 * <p>Uses a cost factor of 12, which provides strong resistance to brute-force
 * attacks while remaining practical for login flows.
 */
public class BcryptPasswordHasher implements PasswordHasher {

    private static final int COST_FACTOR = 12;

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if {@code plainPassword} is null or blank
     */
    @Override
    public String hash(String plainPassword) {
        validatePassword(plainPassword);
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST_FACTOR));
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if {@code plainPassword} or {@code hash} is null
     */
    @Override
    public boolean verify(String plainPassword, String hash) {
        validateNotNull(plainPassword, "plainPassword");
        validateNotNull(hash, "hash");
        return BCrypt.checkpw(plainPassword, hash);
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be null or blank");
        }
    }

    private void validateNotNull(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
