package com.liftit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integrationTest")
class AppIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void applicationContextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void liquibaseCreatesUsersTable() {
        // Given / When - Liquibase runs automatically on startup
        // Then - users table exists with the correct columns (Auth0-based schema, no password/username)
        List<String> columnNames = jdbcTemplate.queryForList(
                "SELECT column_name FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = 'users' " +
                "ORDER BY column_name",
                String.class
        );

        assertThat(columnNames).containsExactly(
                "auth0_id", "created_at", "created_by", "email", "id", "updated_at", "updated_by"
        );
    }

    @Test
    void liquibaseSeedsSystemAdminUser() {
        // Given / When - Liquibase seeds the system admin on startup
        // Then - exactly one system admin row exists with id=1
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = 1 AND auth0_id = 'system'",
                Integer.class
        );

        assertThat(count).isEqualTo(1);
    }
}
