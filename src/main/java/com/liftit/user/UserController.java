package com.liftit.user;

import com.liftit.user.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user provisioning and profile management.
 *
 * <h3>Identity resolution</h3>
 * <p>Profile endpoints identify the caller from the JWT {@code sub} claim, which the
 * {@link com.liftit.auth.AuthenticationFilter} extracts and stores as the principal in
 * {@link SecurityContextHolder}. User identity is <em>never</em> accepted from request
 * headers or the request body — callers cannot supply their own {@code userId}
 * (IDOR prevention).
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserProvisioningService userProvisioningService;
    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    public UserController(
            UserProvisioningService userProvisioningService,
            UserProfileService userProfileService,
            UserRepository userRepository) {
        this.userProvisioningService = userProvisioningService;
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
    }

    /**
     * Provisions a local user row from Auth0 identity claims.
     *
     * <p>The frontend should call this endpoint once after the user's first
     * successful Auth0 login. Subsequent calls with the same {@code auth0Id}
     * or {@code email} will receive {@code 409 Conflict}.
     *
     * @param request the Auth0 subject and email from the JWT claims
     * @return {@code 201 Created} with the provisioned user, or an error status
     */
    @PostMapping("/me")
    public ResponseEntity<UserResponse> provisionUser(@RequestBody ProvisionUserRequest request) {
        validateProvisionRequest(request);
        User user = userProvisioningService.provision(
                Auth0Id.of(request.auth0Id()),
                Email.of(request.email())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    /**
     * Creates a lifting profile for the authenticated user.
     *
     * <p>Each user may have at most one profile. A second call returns {@code 409 Conflict}.
     * The caller is identified from the JWT {@code sub} claim in the
     * {@link SecurityContextHolder}, placed there by
     * {@link com.liftit.auth.AuthenticationFilter}.
     *
     * @param request the profile fields supplied during onboarding
     * @return {@code 201 Created} with the created profile, or an error status
     */
    @PostMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> createProfile(
            @RequestBody CreateUserProfileRequest request) {
        Long userId = resolveUserId();
        UserProfile profile = userProfileService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserProfileResponse.from(profile));
    }

    /**
     * Returns the lifting profile for the authenticated user.
     *
     * <p>The caller is identified from the JWT {@code sub} claim in the
     * {@link SecurityContextHolder}, placed there by
     * {@link com.liftit.auth.AuthenticationFilter}.
     *
     * @return {@code 200 OK} with the profile, or {@code 404 Not Found} if no profile exists
     */
    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Long userId = resolveUserId();
        return userProfileService.getProfile(userId)
                .map(UserProfileResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Resolves the internal userId from the JWT principal stored in the security context.
     *
     * <p>The {@link com.liftit.auth.AuthenticationFilter} places the Auth0 subject
     * string as the authentication principal before this controller is invoked.
     * Returns {@code 401 Unauthorized} if no authentication is present or the
     * auth0Id is not found in the users table.
     */
    private Long resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException();
        }
        return userRepository.findByAuth0Id(Auth0Id.of(authentication.getName()))
                .map(User::id)
                .orElseThrow(UnauthorizedException::new);
    }

    private void validateProvisionRequest(ProvisionUserRequest request) {
        if (request.auth0Id() == null || request.auth0Id().isBlank()) {
            throw new IllegalArgumentException("auth0Id must not be blank");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }
}
