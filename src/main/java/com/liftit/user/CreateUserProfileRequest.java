package com.liftit.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request body for {@code POST /api/v1/users/me/profile}.
 *
 * <p>The caller supplies the lifting-specific profile fields during onboarding.
 * The authenticated user's identity is resolved server-side from the JWT {@code sub}
 * claim — it is never accepted from the request body (IDOR prevention).
 *
 * <p>The app is imperial-only — all measurements are in imperial units.
 *
 * @param username    user-chosen handle; required, 1–30 characters, globally unique
 * @param displayName optional display name; max 100 characters
 * @param gender      optional; one of: {@code male}, {@code female},
 *                    {@code non_binary}, {@code prefer_not_to_say}
 * @param birthdate   optional ISO 8601 date ({@code YYYY-MM-DD})
 * @param heightIn    optional height in inches
 */
public record CreateUserProfileRequest(
        @NotBlank(message = "username must not be blank")
        @Size(max = 30, message = "username must be 30 characters or fewer")
        String username,

        @Size(max = 100, message = "displayName must be 100 characters or fewer")
        String displayName,

        @Pattern(
                regexp = "^(male|female|non_binary|prefer_not_to_say)?$",
                message = "gender must be one of: male, female, non_binary, prefer_not_to_say"
        )
        String gender,

        LocalDate birthdate,

        Double heightIn
) {
}
