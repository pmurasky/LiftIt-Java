package com.liftit.auth;

import com.liftit.auth.exception.AuthenticationException;
import com.liftit.auth.exception.InvalidTokenException;
import com.liftit.user.Auth0Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationServiceImplTest {

    private AuthenticationStrategy strategy;
    private JwtAuthenticationServiceImpl service;

    @BeforeEach
    void setUp() {
        strategy = mock(AuthenticationStrategy.class);
        service = new JwtAuthenticationServiceImpl(List.of(strategy));
    }

    // ── authenticate ────────────────────────────────────────────────────────

    @Test
    void shouldDelegateToMatchingStrategy() {
        // Given
        Credentials credentials = Credentials.bearer("valid.jwt.token");
        AuthenticationResult expected = AuthenticationResult.of(
                Token.of("valid.jwt.token"),
                Auth0Id.of("auth0|abc123")
        );
        when(strategy.supports(credentials)).thenReturn(true);
        when(strategy.execute(credentials)).thenReturn(expected);

        // When
        AuthenticationResult result = service.authenticate(credentials);

        // Then
        assertEquals(expected, result);
        verify(strategy).execute(credentials);
    }

    @Test
    void shouldThrowWhenNoStrategySupportsCredentials() {
        // Given
        Credentials credentials = Credentials.bearer("some.jwt.token");
        when(strategy.supports(credentials)).thenReturn(false);

        // When / Then
        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> service.authenticate(credentials)
        );
        assertEquals("Authentication failed: no strategy supports the provided credentials", ex.getMessage());
    }

    @Test
    void shouldThrowWhenCredentialsAreNull() {
        // Given / When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.authenticate(null)
        );
    }

    @Test
    void shouldPropagateInvalidTokenExceptionFromStrategy() {
        // Given
        Credentials credentials = Credentials.bearer("expired.jwt.token");
        when(strategy.supports(credentials)).thenReturn(true);
        when(strategy.execute(credentials)).thenThrow(InvalidTokenException.expired());

        // When / Then
        InvalidTokenException ex = assertThrows(
                InvalidTokenException.class,
                () -> service.authenticate(credentials)
        );
        assertEquals("Token is expired", ex.getMessage());
    }

    @Test
    void shouldUseFirstMatchingStrategyWhenMultipleExist() {
        // Given
        AuthenticationStrategy second = mock(AuthenticationStrategy.class);
        service = new JwtAuthenticationServiceImpl(List.of(strategy, second));

        Credentials credentials = Credentials.bearer("valid.jwt.token");
        AuthenticationResult expected = AuthenticationResult.of(
                Token.of("valid.jwt.token"),
                Auth0Id.of("auth0|abc123")
        );
        when(strategy.supports(credentials)).thenReturn(true);
        when(strategy.execute(credentials)).thenReturn(expected);
        when(second.supports(credentials)).thenReturn(true);

        // When
        service.authenticate(credentials);

        // Then — first strategy executed, second never called
        verify(strategy).execute(credentials);
        verify(second, never()).execute(any());
    }

    // ── validateToken ───────────────────────────────────────────────────────

    @Test
    void shouldReturnTrueForValidToken() {
        // Given
        Token token = Token.of("valid.jwt.token");
        Credentials credentials = Credentials.bearer(token.value());
        AuthenticationResult result = AuthenticationResult.of(token, Auth0Id.of("auth0|abc123"));
        when(strategy.supports(credentials)).thenReturn(true);
        when(strategy.execute(credentials)).thenReturn(result);

        // When
        boolean valid = service.validateToken(token);

        // Then
        assertTrue(valid);
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        // Given
        Token token = Token.of("expired.jwt.token");
        Credentials credentials = Credentials.bearer(token.value());
        when(strategy.supports(credentials)).thenReturn(true);
        when(strategy.execute(credentials)).thenThrow(InvalidTokenException.expired());

        // When
        boolean valid = service.validateToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    void shouldReturnFalseForMalformedToken() {
        // Given
        Token token = Token.of("bad.token");
        Credentials credentials = Credentials.bearer(token.value());
        when(strategy.supports(credentials)).thenReturn(true);
        when(strategy.execute(credentials)).thenThrow(InvalidTokenException.malformed());

        // When
        boolean valid = service.validateToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    void shouldReturnFalseWhenNoStrategySupportsToken() {
        // Given
        Token token = Token.of("unsupported.token");
        when(strategy.supports(any())).thenReturn(false);

        // When
        boolean valid = service.validateToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    void shouldThrowWhenTokenIsNullInValidateToken() {
        // Given / When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.validateToken(null)
        );
    }

    // ── logout ───────────────────────────────────────────────────────────────

    @Test
    void shouldCompleteLogoutWithoutError() {
        // Given
        Token token = Token.of("any.jwt.token");

        // When / Then — stateless JWT: logout is a no-op, no exception thrown
        assertDoesNotThrow(() -> service.logout(token));
    }

    @Test
    void shouldThrowWhenTokenIsNullInLogout() {
        // Given / When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.logout(null)
        );
    }
}
