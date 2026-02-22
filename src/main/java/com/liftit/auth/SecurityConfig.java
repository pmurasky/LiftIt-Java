package com.liftit.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/**
 * Spring Security configuration for the LiftIt API.
 *
 * <p>Configures a stateless JWT-based security filter chain using the custom
 * {@link AuthenticationFilter}. Public endpoints (e.g. user provisioning) are
 * excluded from authentication; all other API routes require a valid bearer token.
 *
 * <p>The RSA public key for JWT signature verification is loaded from the
 * {@code security.jwt.public-key} property (PEM-encoded PKCS#8 SubjectPublicKeyInfo).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String rsaPublicKeyPem;

    public SecurityConfig(
            @Value("${security.jwt.public-key}") String rsaPublicKeyPem) {
        this.rsaPublicKeyPem = rsaPublicKeyPem;
    }

    /**
     * Configures the security filter chain.
     *
     * <p>Public endpoints:
     * <ul>
     *   <li>{@code POST /api/v1/users/me} — user provisioning (bootstrap after first Auth0 login)
     * </ul>
     * All other {@code /api/**} routes require a valid JWT bearer token.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, AuthenticationFilter authFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /** Validates JWT bearer tokens using the configured RSA public key. */
    @Bean
    public AuthenticationService authenticationService() {
        RSAPublicKey publicKey = parsePublicKey(rsaPublicKeyPem);
        JwtAuthenticationStrategy strategy = new JwtAuthenticationStrategy(publicKey);
        return new JwtAuthenticationServiceImpl(List.of(strategy));
    }

    /** Extracts raw token strings from Authorization header values. */
    @Bean
    public BearerTokenExtractor bearerTokenExtractor() {
        return new BearerTokenExtractor();
    }

    /** The filter that intercepts requests and validates bearer tokens. */
    @Bean
    public AuthenticationFilter authenticationFilter(
            AuthenticationService authenticationService,
            BearerTokenExtractor bearerTokenExtractor) {
        return new AuthenticationFilter(authenticationService, bearerTokenExtractor);
    }

    private RSAPublicKey parsePublicKey(String pem) {
        try {
            String stripped = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(stripped);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse RSA public key from PEM: " + e.getMessage(), e);
        }
    }
}
