package com.liftit.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private static RSAPublicKey rsaPublicKey;
    private static String pemPublicKey;

    @BeforeAll
    static void generateTestKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        String encoded = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        pemPublicKey = "-----BEGIN PUBLIC KEY-----\n" + encoded + "\n-----END PUBLIC KEY-----";
    }

    @Test
    void shouldCreateAuthenticationServiceBean() {
        // Given
        SecurityConfig config = new SecurityConfig(pemPublicKey);

        // When
        AuthenticationService service = config.authenticationService();

        // Then
        assertNotNull(service);
        assertInstanceOf(JwtAuthenticationServiceImpl.class, service);
    }

    @Test
    void shouldCreateBearerTokenExtractorBean() {
        // Given
        SecurityConfig config = new SecurityConfig(pemPublicKey);

        // When
        BearerTokenExtractor extractor = config.bearerTokenExtractor();

        // Then
        assertNotNull(extractor);
    }

    @Test
    void shouldCreateAuthenticationFilterBean() {
        // Given
        SecurityConfig config = new SecurityConfig(pemPublicKey);
        AuthenticationService service = config.authenticationService();
        BearerTokenExtractor extractor = config.bearerTokenExtractor();

        // When
        AuthenticationFilter filter = config.authenticationFilter(service, extractor);

        // Then
        assertNotNull(filter);
    }

    @Test
    void shouldThrowWhenPublicKeyPemIsMalformed() {
        // Given
        SecurityConfig config = new SecurityConfig("not-a-valid-pem");

        // When / Then
        assertThrows(IllegalStateException.class, config::authenticationService);
    }
}
