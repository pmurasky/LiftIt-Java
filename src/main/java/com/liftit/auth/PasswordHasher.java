package com.liftit.auth;

/**
 * Contract for hashing and verifying passwords.
 *
 * <p>Implementations must use a strong adaptive hashing algorithm (e.g. BCrypt, Argon2)
 * with per-password salting and a cost factor that resists brute-force attacks.
 * Passwords must never be logged or stored in plain text.
 */
public interface PasswordHasher {

    /**
     * Hashes a plain-text password.
     *
     * @param plainPassword the raw password; must not be null or blank
     * @return the hashed password string, including algorithm and salt
     * @throws IllegalArgumentException if {@code plainPassword} is null or blank
     */
    String hash(String plainPassword);

    /**
     * Verifies a plain-text password against a previously generated hash.
     *
     * <p>Implementations must use constant-time comparison to prevent timing attacks.
     *
     * @param plainPassword the raw password to check; must not be null
     * @param hash          the stored hash to compare against; must not be null
     * @return {@code true} if the password matches the hash, {@code false} otherwise
     * @throws IllegalArgumentException if {@code plainPassword} or {@code hash} is null
     */
    boolean verify(String plainPassword, String hash);
}
