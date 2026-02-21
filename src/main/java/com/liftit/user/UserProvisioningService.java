package com.liftit.user;

/**
 * Contract for provisioning application users from Auth0 identity claims.
 *
 * <p>The backend never handles registration directly — Auth0 owns credentials
 * and the login flow. On first authenticated request, the frontend calls
 * {@code POST /api/v1/users/me} so the backend can create a local {@code users}
 * row linked to the Auth0 identity.
 *
 * <p>Callers depend on this interface rather than any concrete implementation
 * (Dependency Inversion Principle).
 */
public interface UserProvisioningService {

    /**
     * Provisions a new user from Auth0 identity claims.
     *
     * <p>If a user with the given {@code auth0Id} already exists, a
     * {@link DuplicateUserException} is thrown. If a user with the given
     * {@code email} already exists (different Auth0 account, same email),
     * a {@link DuplicateUserException} is thrown.
     *
     * @param auth0Id the Auth0 subject identifier from the validated JWT; must not be null
     * @param email   the email address from the validated JWT claims; must not be null
     * @return the newly created {@link User}
     * @throws DuplicateUserException   if a user with the given auth0Id or email already exists
     * @throws IllegalArgumentException if auth0Id or email is null
     */
    User provision(Auth0Id auth0Id, Email email);
}
