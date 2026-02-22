package com.liftit.auth;

import com.liftit.auth.exception.AuthenticationException;
import com.liftit.auth.exception.InvalidTokenException;

/**
 * Strategy interface for a single authentication method.
 *
 * <p>Implementations handle one specific type of credentials (e.g. JWT bearer tokens).
 * The {@link AuthenticationService} iterates over all registered strategies and
 * delegates to the first one that {@link #supports(Credentials)} the given credentials.
 *
 * <p>This follows the Strategy Pattern (OCP): new authentication methods can be added
 * by creating a new implementation — no modifications to existing classes are required.
 *
 * <p>Example implementation:
 * <pre>
 *   public class JwtAuthenticationStrategy implements AuthenticationStrategy {
 *
 *       {@literal @}Override
 *       public boolean supports(Credentials credentials) {
 *           return credentials.type() == Credentials.CredentialType.BEARER;
 *       }
 *
 *       {@literal @}Override
 *       public AuthenticationResult execute(Credentials credentials) {
 *           // validate JWT, extract claims, return result
 *       }
 *   }
 * </pre>
 */
public interface AuthenticationStrategy {

    /**
     * Returns {@code true} if this strategy can handle the given credentials.
     *
     * <p>Implementations should base the decision solely on the credential type,
     * not on the credential value — value validation happens in {@link #execute}.
     *
     * @param credentials the credentials to check; never null
     * @return {@code true} if this strategy can authenticate the given credentials
     */
    boolean supports(Credentials credentials);

    /**
     * Authenticates the given credentials and returns the result.
     *
     * <p>Only called when {@link #supports(Credentials)} returned {@code true}.
     *
     * @param credentials the credentials to authenticate; never null
     * @return a successful {@link AuthenticationResult} containing the validated
     *         token and resolved principal
     * @throws AuthenticationException if the credentials are invalid
     * @throws InvalidTokenException   if the token is present but malformed, expired,
     *                                 or the signature does not verify
     */
    AuthenticationResult execute(Credentials credentials);
}
