package com.liftit.auth;

/**
 * Value object representing raw credentials presented by a client during authentication.
 *
 * <p>Credentials are opaque at the domain level — they carry the raw value (e.g. a
 * JWT bearer token string) and the type that signals which {@code AuthenticationStrategy}
 * should handle them. No validation of the credential value is performed here; that is
 * the responsibility of the strategy.
 *
 * <p>Examples:
 * <pre>
 *   Credentials.bearer("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");
 * </pre>
 */
public record Credentials(String value, CredentialType type) {

    /**
     * Canonical constructor — validates that value and type are non-null.
     *
     * @param value the raw credential string; must not be null or blank
     * @param type  the credential type; must not be null
     * @throws IllegalArgumentException if {@code value} is null or blank, or {@code type} is null
     */
    public Credentials {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Credentials value must not be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Credentials type must not be null");
        }
    }

    /**
     * Creates {@code Credentials} for a JWT bearer token.
     *
     * @param token the raw JWT string; must not be null or blank
     * @return a new {@code Credentials} of type {@link CredentialType#BEARER}
     * @throws IllegalArgumentException if {@code token} is null or blank
     */
    public static Credentials bearer(String token) {
        return new Credentials(token, CredentialType.BEARER);
    }

    /**
     * The type of credential, used by {@code AuthenticationStrategy} to determine
     * whether it can handle a given set of credentials.
     */
    public enum CredentialType {
        /** A JWT bearer token (Authorization: Bearer ...) */
        BEARER
    }
}
