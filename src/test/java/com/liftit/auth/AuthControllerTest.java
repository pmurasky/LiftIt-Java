package com.liftit.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthControllerTest {

    private final AuthController controller = new AuthController();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuth0IdForAuthenticatedUser() {
        // Given
        String auth0Id = "auth0|abc123";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(auth0Id, null, List.of()));

        // When
        ResponseEntity<MeResponse> response = controller.me();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().auth0Id()).isEqualTo(auth0Id);
    }

    @Test
    void shouldReturn401WhenSecurityContextHasNoAuthentication() {
        // Given — no authentication in context
        SecurityContextHolder.clearContext();

        // When / Then
        assertThrows(IllegalStateException.class, () -> controller.me());
    }

    @Test
    void shouldReturn204ForLogout() {
        // Given — logout is a stateless no-op
        // When
        ResponseEntity<Void> response = controller.logout();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }
}
