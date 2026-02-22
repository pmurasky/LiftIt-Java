package com.liftit.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/** Test-only JWT factory for integration tests. */
public final class JwtTestTokenFactory {

    private static final KeyPair KEY_PAIR = generateKeyPair();

    private JwtTestTokenFactory() {
    }

    public static String bearerToken(String subject) {
        return "Bearer " + rawToken(subject);
    }

    public static String rawToken(String subject) {
        try {
            SignedJWT jwt = new SignedJWT(header(), claims(subject));
            jwt.sign(new RSASSASigner(KEY_PAIR.getPrivate()));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to create signed JWT for integration test", e);
        }
    }

    public static String publicKeyPem() {
        byte[] encoded = KEY_PAIR.getPublic().getEncoded();
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(encoded);
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            validatePublicKey(keyPair);
            return keyPair;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair for integration test", e);
        }
    }

    private static void validatePublicKey(KeyPair keyPair) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
        KeyFactory factory = KeyFactory.getInstance("RSA");
        factory.generatePublic(spec);
    }

    private static JWSHeader header() {
        return new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
    }

    private static JWTClaimsSet claims(String subject) {
        Instant now = Instant.now();
        return new JWTClaimsSet.Builder()
                .subject(subject)
                .issueTime(Date.from(now.minusSeconds(10)))
                .expirationTime(Date.from(now.plusSeconds(3600)))
                .build();
    }
}
