package com.liftit.auth;

import com.liftit.auth.exception.InvalidTokenException;
import com.liftit.user.Auth0Id;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

/**
 * JWT-based {@link AuthenticationStrategy} implementation.
 *
 * <p>Validates RS256-signed JWTs using the provided RSA public key. On success,
 * extracts the {@code sub} claim as the {@link Auth0Id} of the authenticated principal.
 *
 * <p>Throws {@link InvalidTokenException} for expired, malformed, or signature-invalid tokens.
 */
public class JwtAuthenticationStrategy implements AuthenticationStrategy {

    private final RSASSAVerifier verifier;

    /**
     * @param publicKey the RSA public key used to verify JWT signatures
     */
    public JwtAuthenticationStrategy(RSAPublicKey publicKey) {
        this.verifier = new RSASSAVerifier(publicKey);
    }

    /** Supports only {@link Credentials.CredentialType#BEARER} credentials. */
    @Override
    public boolean supports(Credentials credentials) {
        return credentials.type() == Credentials.CredentialType.BEARER;
    }

    /**
     * Parses, verifies, and validates the JWT bearer token.
     *
     * @throws InvalidTokenException if the token is malformed, expired, or has an invalid signature
     */
    @Override
    public AuthenticationResult execute(Credentials credentials) {
        SignedJWT jwt = parse(credentials.value());
        verifySignature(jwt);
        verifyNotExpired(jwt);
        Auth0Id auth0Id = extractSubject(jwt);
        return AuthenticationResult.of(Token.of(credentials.value()), auth0Id);
    }

    private SignedJWT parse(String raw) {
        try {
            return SignedJWT.parse(raw);
        } catch (ParseException e) {
            throw InvalidTokenException.malformed();
        }
    }

    private void verifySignature(SignedJWT jwt) {
        try {
            if (!jwt.verify(verifier)) {
                throw InvalidTokenException.invalidSignature();
            }
        } catch (JOSEException e) {
            throw InvalidTokenException.invalidSignature();
        }
    }

    private void verifyNotExpired(SignedJWT jwt) {
        try {
            Date expiration = jwt.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                throw InvalidTokenException.expired();
            }
        } catch (ParseException e) {
            throw InvalidTokenException.malformed();
        }
    }

    private Auth0Id extractSubject(SignedJWT jwt) {
        try {
            String subject = jwt.getJWTClaimsSet().getSubject();
            if (subject == null || subject.isBlank()) {
                throw InvalidTokenException.malformed();
            }
            return Auth0Id.of(subject);
        } catch (ParseException e) {
            throw InvalidTokenException.malformed();
        }
    }
}
