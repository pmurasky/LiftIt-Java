package com.liftit.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user provisioning.
 *
 * <p>The frontend calls {@code POST /api/v1/users/me} on first login,
 * passing the Auth0 subject identifier and email extracted from the JWT.
 * The backend provisions a local application user row and returns it.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserProvisioningService userProvisioningService;

    public UserController(UserProvisioningService userProvisioningService) {
        this.userProvisioningService = userProvisioningService;
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
        validateRequest(request);
        User user = userProvisioningService.provision(
                Auth0Id.of(request.auth0Id()),
                Email.of(request.email())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Void> handleDuplicateUser(DuplicateUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    private void validateRequest(ProvisionUserRequest request) {
        if (request.auth0Id() == null || request.auth0Id().isBlank()) {
            throw new IllegalArgumentException("auth0Id must not be blank");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }
}
