package com.liftit.user;

import java.time.LocalDate;

/**
 * Request body for {@code POST /api/v1/users/me/profile}.
 *
 * <p>The caller supplies the lifting-specific profile fields during onboarding.
 * The authenticated user's identity is resolved server-side from the JWT {@code sub}
 * claim — it is never accepted from the request body (IDOR prevention).
 *
 * @param username        user-chosen handle; required, 1–30 characters, globally unique
 * @param displayName     optional display name; max 100 characters
 * @param gender          optional; one of: {@code male}, {@code female},
 *                        {@code non_binary}, {@code prefer_not_to_say}
 * @param birthdate       optional ISO 8601 date ({@code YYYY-MM-DD})
 * @param heightCm        optional height in centimetres; always stored metric
 * @param unitsPreference required; {@code metric} or {@code imperial}
 */
public record CreateUserProfileRequest(
        String username,
        String displayName,
        String gender,
        LocalDate birthdate,
        Double heightCm,
        String unitsPreference
) {
}
