package com.liftit.user;

import com.liftit.user.exception.DuplicateProfileException;
import com.liftit.user.exception.DuplicateUserException;
import com.liftit.user.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user provisioning and profile management.
 *
 * <h3>Security note — X-Auth0-Id header</h3>
 * <p>Profile endpoints currently identify the caller via the {@code X-Auth0-Id} request
 * header. This is an <strong>explicit temporary stub</strong> that will be replaced when
 * the Auth0 JWT resource-server filter (epic #14) is implemented. At that point the
 * controller will extract the {@code sub} claim from
 * {@code SecurityContextHolder.getContext().getAuthentication()}, and this header will be
 * removed. The stub is intentionally named and documented so it is never mistaken for a
 * production authentication mechanism.
 *
 * <p>User identity is <em>never</em> accepted from the request body — callers cannot
 * supply their own {@code userId} (IDOR prevention).
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
     * The caller is identified via the {@code X-Auth0-Id} header (temporary stub —
     * see class-level Javadoc).
     *
     * @param auth0IdHeader the Auth0 subject identifier (temporary stub for JWT sub claim)
     * @param request       the profile fields supplied during onboarding
     * @return {@code 201 Created} with the created profile, or an error status
     */
    @PostMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> createProfile(
            @RequestHeader(value = "X-Auth0-Id", required = false) String auth0IdHeader,
            @RequestBody CreateUserProfileRequest request) {
        Long userId = resolveUserId(auth0IdHeader);
        UserProfile profile = userProfileService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserProfileResponse.from(profile));
    }

    /**
     * Returns the lifting profile for the authenticated user.
     *
     * <p>The caller is identified via the {@code X-Auth0-Id} header (temporary stub —
     * see class-level Javadoc).
     *
     * @param auth0IdHeader the Auth0 subject identifier (temporary stub for JWT sub claim)
     * @return {@code 200 OK} with the profile, or {@code 404 Not Found} if no profile exists
     */
    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader(value = "X-Auth0-Id", required = false) String auth0IdHeader) {
        Long userId = resolveUserId(auth0IdHeader);
        return userProfileService.getProfile(userId)
                .map(UserProfileResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Void> handleDuplicateUser(DuplicateUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(DuplicateProfileException.class)
    public ResponseEntity<Void> handleDuplicateProfile(DuplicateProfileException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Void> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Resolves the internal userId from the temporary auth header stub.
     *
     * <p>Returns {@code 401 Unauthorized} if the header is absent or the auth0Id
     * is not found in the users table. This will be replaced by JWT principal
     * extraction when epic #14 is implemented.
     */
    private Long resolveUserId(String auth0IdHeader) {
        if (auth0IdHeader == null || auth0IdHeader.isBlank()) {
            throw new UnauthorizedException();
        }
        return userRepository.findByAuth0Id(Auth0Id.of(auth0IdHeader))
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
