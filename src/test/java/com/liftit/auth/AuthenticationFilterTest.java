package com.liftit.auth;

import com.liftit.auth.exception.AuthenticationException;
import com.liftit.auth.exception.InvalidTokenException;
import com.liftit.user.Auth0Id;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    private AuthenticationService authService;
    private BearerTokenExtractor extractor;
    private AuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        authService = mock(AuthenticationService.class);
        extractor = mock(BearerTokenExtractor.class);
        filter = new AuthenticationFilter(authService, extractor);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPopulateSecurityContextAndContinueChainForValidToken() throws ServletException, IOException {
        // Given
        String rawToken = "valid.jwt.token";
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        AuthenticationResult result = AuthenticationResult.of(Token.of(rawToken), auth0Id);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + rawToken);
        when(extractor.extract("Bearer " + rawToken)).thenReturn(java.util.Optional.of(rawToken));
        when(authService.authenticate(Credentials.bearer(rawToken))).thenReturn(result);

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(chain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("auth0|abc123",
                SecurityContextHolder.getContext().getAuthentication().getName());
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void shouldReturn401AndNotContinueChainWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(extractor.extract(null)).thenReturn(java.util.Optional.empty());

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
        verify(chain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldReturn401AndNotContinueChainWhenTokenIsExpired()
            throws ServletException, IOException {
        // Given
        String rawToken = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + rawToken);
        when(extractor.extract("Bearer " + rawToken)).thenReturn(java.util.Optional.of(rawToken));
        when(authService.authenticate(Credentials.bearer(rawToken)))
                .thenThrow(InvalidTokenException.expired());

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldReturn401AndNotContinueChainWhenTokenIsMalformed()
            throws ServletException, IOException {
        // Given
        String rawToken = "malformed.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + rawToken);
        when(extractor.extract("Bearer " + rawToken)).thenReturn(java.util.Optional.of(rawToken));
        when(authService.authenticate(Credentials.bearer(rawToken)))
                .thenThrow(InvalidTokenException.malformed());

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldReturn401AndNotContinueChainWhenTokenHasInvalidSignature()
            throws ServletException, IOException {
        // Given
        String rawToken = "tampered.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + rawToken);
        when(extractor.extract("Bearer " + rawToken)).thenReturn(java.util.Optional.of(rawToken));
        when(authService.authenticate(Credentials.bearer(rawToken)))
                .thenThrow(InvalidTokenException.invalidSignature());

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldReturn401AndNotContinueChainWhenAuthenticationFails()
            throws ServletException, IOException {
        // Given
        String rawToken = "any.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + rawToken);
        when(extractor.extract("Bearer " + rawToken)).thenReturn(java.util.Optional.of(rawToken));
        when(authService.authenticate(Credentials.bearer(rawToken)))
                .thenThrow(AuthenticationException.invalidCredentials());

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(any(), any());
    }
}
