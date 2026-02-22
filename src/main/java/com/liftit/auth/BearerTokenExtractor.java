package com.liftit.auth;

import java.util.Optional;

/**
 * Extracts the raw JWT token string from an HTTP {@code Authorization} header.
 *
 * <p>Supports the standard Bearer token format:
 * {@code Authorization: Bearer <token>}
 *
 * <p>The prefix check is case-insensitive to tolerate minor formatting differences.
 * Returns an empty Optional for missing, blank, or non-Bearer headers.
 */
public class BearerTokenExtractor {

    private static final String BEARER_PREFIX = "bearer ";

    /**
     * Extracts the raw token from the given Authorization header value.
     *
     * @param authorizationHeader the value of the Authorization header; may be null
     * @return the raw token string, or empty if the header is absent or not Bearer format
     */
    public Optional<String> extract(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return Optional.empty();
        }
        String lower = authorizationHeader.toLowerCase();
        if (!lower.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).strip();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }
}
