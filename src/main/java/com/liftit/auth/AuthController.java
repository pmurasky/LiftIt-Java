package com.liftit.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication introspection and session management.
 *
 * <h3>Endpoints</h3>
 * <ul>
 *   <li>{@code GET  /api/auth/me}      — returns the identity of the authenticated caller
 *   <li>{@code POST /api/auth/logout}  — stateless logout (no-op; client discards the token)
 * </ul>
 *
 * <p>Authentication is handled upstream by {@link AuthenticationFilter}, which validates
 * the JWT and populates {@link SecurityContextHolder} before this controller is invoked.
 * The principal stored in the security context is the raw Auth0 subject string (e.g.
 * {@code "auth0|abc123"}).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Returns the identity of the currently authenticated user.
     *
     * <p>The Auth0 subject is extracted from the {@link SecurityContextHolder};
     * it was placed there by {@link AuthenticationFilter} after JWT validation.
     *
     * @return {@code 200 OK} with {@link MeResponse} containing the caller's Auth0 subject
     * @throws IllegalStateException if the security context contains no authentication
     *                               (should not happen when the filter chain is correctly configured)
     */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context");
        }
        return ResponseEntity.ok(new MeResponse(authentication.getName()));
    }

    /**
     * Stateless logout — instructs the client to discard its token.
     *
     * <p>JWTs are self-contained and expire naturally. This endpoint is a no-op on the server
     * side; the client is responsible for discarding the token. A future decorator can introduce
     * a server-side token denylist if revocation before expiry is required.
     *
     * @return {@code 204 No Content}
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
