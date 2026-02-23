package com.liftit.auth;

import com.liftit.auth.exception.AuthenticationException;
import com.liftit.auth.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet filter that authenticates incoming requests via JWT bearer tokens.
 *
 * <p>Intercepts every request once per dispatch. Extracts the bearer token from
 * the {@code Authorization} header, validates it using {@link AuthenticationService},
 * and populates the {@link SecurityContextHolder} on success.
 *
 * <p>The user-provisioning endpoint ({@code POST /api/v1/users/me}) is excluded from
 * token validation via {@link #shouldNotFilter}. Login, registration, and token refresh
 * are handled entirely by Auth0's hosted UI — the client never calls this server for those.
 *
 * <p>Returns {@code 401 Unauthorized} immediately for missing or invalid tokens
 * without invoking downstream filters.
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String MISSING_HEADER_MSG = "Missing or invalid Authorization header";
    private static final String INVALID_TOKEN_MSG = "Invalid or expired token";

    private static final String PUBLIC_URI = "/api/v1/users/me";

    private final AuthenticationService authenticationService;
    private final BearerTokenExtractor tokenExtractor;

    /**
     * @param authenticationService validates JWT bearer tokens; must not be null
     * @param tokenExtractor        extracts token strings from header values; must not be null
     */
    public AuthenticationFilter(
            AuthenticationService authenticationService,
            BearerTokenExtractor tokenExtractor) {
        this.authenticationService = authenticationService;
        this.tokenExtractor = tokenExtractor;
    }

    /**
     * Skips token validation for the user-provisioning endpoint.
     *
     * <p>Only {@code POST /api/v1/users/me} is public; all other paths require a bearer token.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                && PUBLIC_URI.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String headerValue = request.getHeader(AUTHORIZATION_HEADER);
        Optional<String> rawToken = tokenExtractor.extract(headerValue);
        if (rawToken.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MISSING_HEADER_MSG);
            return;
        }
        try {
            AuthenticationResult result = authenticationService.authenticate(
                    Credentials.bearer(rawToken.get()));
            populateSecurityContext(result);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException | AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_TOKEN_MSG);
        }
    }

    private void populateSecurityContext(AuthenticationResult result) {
        String principal = result.auth0Id().value();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
