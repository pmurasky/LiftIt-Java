package com.liftit.auth;

import com.liftit.user.Auth0Id;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationResultTest {

    private static final Token VALID_TOKEN = Token.of("eyJhbGciOiJSUzI1NiJ9.payload.sig");
    private static final Auth0Id VALID_AUTH0_ID = Auth0Id.of("auth0|abc123");

    @Test
    void shouldCreateResultWithTokenAndAuth0Id() {
        // When
        AuthenticationResult result = AuthenticationResult.of(VALID_TOKEN, VALID_AUTH0_ID);

        // Then
        assertEquals(VALID_TOKEN, result.token());
        assertEquals(VALID_AUTH0_ID, result.auth0Id());
    }

    @Test
    void shouldThrowWhenTokenIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class,
                () -> AuthenticationResult.of(null, VALID_AUTH0_ID));
    }

    @Test
    void shouldThrowWhenAuth0IdIsNull() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class,
                () -> AuthenticationResult.of(VALID_TOKEN, null));
    }

    @Test
    void shouldConsiderEqualWhenSameTokenAndAuth0Id() {
        // Given
        AuthenticationResult a = AuthenticationResult.of(VALID_TOKEN, VALID_AUTH0_ID);
        AuthenticationResult b = AuthenticationResult.of(VALID_TOKEN, VALID_AUTH0_ID);

        // When / Then
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotConsiderEqualWhenDifferentAuth0Id() {
        // Given
        AuthenticationResult a = AuthenticationResult.of(VALID_TOKEN, Auth0Id.of("auth0|user1"));
        AuthenticationResult b = AuthenticationResult.of(VALID_TOKEN, Auth0Id.of("auth0|user2"));

        // When / Then
        assertNotEquals(a, b);
    }
}
