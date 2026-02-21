package com.liftit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the user provisioning flow.
 *
 * <p>Boots the full Spring context with a real Postgres via Testcontainers and
 * exercises the complete HTTP → controller → service → JPA → database stack.
 * Uses real JPA repositories — no mocks.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
class UserProvisioningIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll(
                userJpaRepository.findAll().stream()
                        .filter(e -> e.toDomain().id() > 99)
                        .toList()
        );
    }

    @Test
    void shouldProvisionUserViaEndpointAndReturnCreated() throws Exception {
        // Given
        String body = """
                {"auth0Id": "auth0|integrationuser", "email": "integration@example.com"}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auth0Id").value("auth0|integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void shouldReturn409WhenUserAlreadyExists() throws Exception {
        // Given — provision the user once
        String body = """
                {"auth0Id": "auth0|existing", "email": "existing@example.com"}
                """;
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // When — provision again with the same auth0Id
        // Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }
}
