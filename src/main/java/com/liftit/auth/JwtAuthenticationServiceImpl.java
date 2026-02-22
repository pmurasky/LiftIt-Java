package com.liftit.auth;

import com.liftit.auth.exception.AuthenticationException;

import java.util.List;

/**
 * Stateless JWT implementation of {@link AuthenticationService}.
 *
 * <p>Delegates {@link #authenticate(Credentials)} to the first matching
 * {@link AuthenticationStrategy} in the injected list. Token validation
 * reuses the same delegation path. Logout is a no-op — stateless JWTs
 * expire naturally; a token denylist can be added as a future decorator.
 *
 * <p>Dependencies are constructor-injected (DIP) to support testability
 * and Spring-managed wiring.
 */
public class JwtAuthenticationServiceImpl implements AuthenticationService {

    private final List<AuthenticationStrategy> strategies;

    /**
     * @param strategies ordered list of strategies to try; must not be null or empty
     */
    public JwtAuthenticationServiceImpl(List<AuthenticationStrategy> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    /**
     * Finds the first strategy that {@link AuthenticationStrategy#supports} the
     * credentials and delegates to its {@link AuthenticationStrategy#execute} method.
     *
     * @throws IllegalArgumentException if {@code credentials} is null
     * @throws AuthenticationException  if no strategy supports the credentials
     */
    @Override
    public AuthenticationResult authenticate(Credentials credentials) {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials must not be null");
        }
        return strategies.stream()
                .filter(s -> s.supports(credentials))
                .findFirst()
                .orElseThrow(AuthenticationException::noStrategyFound)
                .execute(credentials);
    }

    /**
     * No-op for stateless JWT — tokens expire naturally.
     * A future decorator can introduce a denylist if needed.
     *
     * @throws IllegalArgumentException if {@code token} is null
     */
    @Override
    public void logout(Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
    }

    /**
     * Returns {@code true} if the token can be successfully authenticated;
     * {@code false} for any exception (expired, malformed, no strategy, etc.).
     *
     * @throws IllegalArgumentException if {@code token} is null
     */
    @Override
    public boolean validateToken(Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        try {
            authenticate(Credentials.bearer(token.value()));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
