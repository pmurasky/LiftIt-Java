package com.liftit.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BearerTokenExtractorTest {

    private BearerTokenExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new BearerTokenExtractor();
    }

    @Test
    void shouldExtractTokenFromValidBearerHeader() {
        // Given
        String header = "Bearer eyJhbGciOiJSUzI1NiJ9.payload.signature";

        // When
        Optional<String> result = extractor.extract(header);

        // Then
        assertTrue(result.isPresent());
        assertEquals("eyJhbGciOiJSUzI1NiJ9.payload.signature", result.get());
    }

    @Test
    void shouldReturnEmptyWhenHeaderIsNull() {
        // Given / When
        Optional<String> result = extractor.extract(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenHeaderIsBlank() {
        // Given / When
        Optional<String> result = extractor.extract("   ");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenHeaderHasNoBearerPrefix() {
        // Given / When
        Optional<String> result = extractor.extract("Basic dXNlcjpwYXNz");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenHeaderIsBearerWithNoToken() {
        // Given / When
        Optional<String> result = extractor.extract("Bearer ");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldBeCaseInsensitiveForBearerPrefix() {
        // Given
        String header = "bearer eyJhbGciOiJSUzI1NiJ9.payload.signature";

        // When
        Optional<String> result = extractor.extract(header);

        // Then
        assertTrue(result.isPresent());
        assertEquals("eyJhbGciOiJSUzI1NiJ9.payload.signature", result.get());
    }
}
