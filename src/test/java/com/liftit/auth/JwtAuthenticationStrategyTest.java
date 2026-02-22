package com.liftit.auth;

import com.liftit.auth.exception.InvalidTokenException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationStrategyTest {

    private RSAPublicKey publicKey;
    private JWSSigner signer;
    private JwtAuthenticationStrategy strategy;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        signer = new RSASSASigner(keyPair.getPrivate());
        strategy = new JwtAuthenticationStrategy(publicKey);
    }

    @Test
    void shouldSupportBearerCredentials() {
        // Given
        Credentials credentials = Credentials.bearer("any.token.value");

        // When
        boolean result = strategy.supports(credentials);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldSupportOnlyBearerCredentialType() {
        // Given — BEARER is the only supported type; strategy is type-gated
        Credentials bearer = Credentials.bearer("any.token.value");

        // When / Then
        assertTrue(strategy.supports(bearer));
    }

    @Test
    void shouldAuthenticateValidJwt() throws Exception {
        // Given
        String subject = "auth0|abc123";
        String rawJwt = buildJwt(subject, futureDate());
        Credentials credentials = Credentials.bearer(rawJwt);

        // When
        AuthenticationResult result = strategy.execute(credentials);

        // Then
        assertEquals(subject, result.auth0Id().value());
        assertEquals(rawJwt, result.token().value());
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsExpired() throws Exception {
        // Given
        String rawJwt = buildJwt("auth0|abc123", pastDate());
        Credentials credentials = Credentials.bearer(rawJwt);

        // When / Then
        InvalidTokenException ex = assertThrows(
                InvalidTokenException.class,
                () -> strategy.execute(credentials)
        );
        assertEquals("Token is expired", ex.getMessage());
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsMalformed() {
        // Given
        Credentials credentials = Credentials.bearer("not.a.valid.jwt.structure");

        // When / Then
        InvalidTokenException ex = assertThrows(
                InvalidTokenException.class,
                () -> strategy.execute(credentials)
        );
        assertEquals("Token is malformed", ex.getMessage());
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenSignatureIsInvalid() throws Exception {
        // Given — sign with a different key pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair otherKeyPair = generator.generateKeyPair();
        JWSSigner otherSigner = new RSASSASigner(otherKeyPair.getPrivate());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("auth0|abc123")
                .expirationTime(futureDate())
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(otherSigner);
        Credentials credentials = Credentials.bearer(jwt.serialize());

        // When / Then
        InvalidTokenException ex = assertThrows(
                InvalidTokenException.class,
                () -> strategy.execute(credentials)
        );
        assertEquals("Token signature is invalid", ex.getMessage());
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenSubjectClaimIsMissing() throws Exception {
        // Given — no subject in claims
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .expirationTime(futureDate())
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(signer);
        Credentials credentials = Credentials.bearer(jwt.serialize());

        // When / Then
        InvalidTokenException ex = assertThrows(
                InvalidTokenException.class,
                () -> strategy.execute(credentials)
        );
        assertEquals("Token is malformed", ex.getMessage());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private String buildJwt(String subject, Date expiration) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .expirationTime(expiration)
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(signer);
        return jwt.serialize();
    }

    private Date futureDate() {
        return Date.from(Instant.now().plusSeconds(3600));
    }

    private Date pastDate() {
        return Date.from(Instant.now().minusSeconds(3600));
    }
}
