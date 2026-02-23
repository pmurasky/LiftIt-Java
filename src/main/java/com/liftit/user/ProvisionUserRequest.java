package com.liftit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for {@code POST /api/v1/users/me}.
 *
 * <p>The frontend extracts {@code auth0Id} from the Auth0 JWT {@code sub} claim
 * and {@code email} from the {@code email} claim, then sends them here to
 * provision a local application user row on first login.
 *
 * @param auth0Id the Auth0 subject identifier ({@code sub} claim); must not be blank
 * @param email   the user's email address ({@code email} claim); must not be blank
 */
public record ProvisionUserRequest(
        @NotBlank(message = "auth0Id must not be blank") String auth0Id,
        @NotBlank(message = "email must not be blank") @Email(message = "email must be valid") String email
) {
}
