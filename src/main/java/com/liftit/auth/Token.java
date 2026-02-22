package com.liftit.auth;

/**
 * Value object representing an opaque authentication token issued to or presented
 * by a client.
 *
 * <p>A {@code Token} carries the raw string value of the token (e.g. a JWT). At the
 * domain level the value is treated as opaque — structural validation (signature,
 * expiry, claims) is the responsibility of the {@code AuthenticationStrategy}.
 *
 * <p>Examples:
 * <pre>
 *   Token token = Token.of("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");
 * </pre>
 */
public record Token(String value) {

    /**
     * Canonical constructor — validates that the token value is non-null and non-blank.
     *
     * @param value the raw token string; must not be null or blank
     * @throws IllegalArgumentException if {@code value} is null or blank
     */
    public Token {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token value must not be null or blank");
        }
    }

    /**
     * Creates a {@code Token} from the given raw token string.
     *
     * @param value the raw token string; must not be null or blank
     * @return a new {@code Token}
     * @throws IllegalArgumentException if {@code value} is null or blank
     */
    public static Token of(String value) {
        return new Token(value);
    }
}
