#!/bin/bash
# Script to create authentication-related GitHub issues
# Requires: GitHub CLI (gh) installed and authenticated
# Usage: ./create-auth-issues.sh

set -e

REPO="pmurasky/LiftIt-Java"

echo "📝 Creating authentication issues for $REPO"
echo ""

# Check if gh is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed."
    echo "Install it with: sudo apt install gh"
    echo "Or visit: https://cli.github.com/"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "❌ Not authenticated with GitHub CLI."
    echo "Run: gh auth login"
    exit 1
fi

# Function to create an issue
create_issue() {
    local title="$1"
    local body="$2"
    local labels="$3"
    
    echo "Creating: $title"
    gh issue create \
        --repo "$REPO" \
        --title "$title" \
        --body "$body" \
        --label "$labels"
}

echo "Creating authentication foundation issues..."

create_issue \
    "Design Authentication Architecture" \
    "## Description
Design the authentication architecture for LiftIt-Java following SOLID principles and engineering standards.

## Requirements
- Support multiple authentication strategies (JWT, OAuth2, Basic Auth)
- Follow Strategy Pattern for extensibility (OCP)
- Separate concerns: authentication vs authorization
- Support for user management
- Secure password storage (bcrypt/argon2)
- Token-based session management

## Deliverables
- [ ] Architecture diagram showing components and interactions
- [ ] Interface definitions for authentication strategies
- [ ] User model design
- [ ] Token/session management approach
- [ ] Security considerations documented

## Acceptance Criteria
- [ ] Architecture follows SOLID principles
- [ ] Design supports multiple auth strategies
- [ ] Clear separation of authentication and authorization
- [ ] Security best practices documented
- [ ] Reviewed and approved by team

## Technical Notes
- Consider using Spring Security or implementing custom solution
- JWT for stateless authentication
- Refresh token strategy for long-lived sessions
- Rate limiting for auth endpoints" \
    "enhancement,priority-high,effort-large,documentation"

create_issue \
    "Implement User Domain Model" \
    "## Description
Create the User domain model following DDD principles and engineering standards.

## Requirements
- User entity with essential fields (id, username, email, password hash)
- Value objects for Email, Password
- Follow SRP - single responsibility per class
- Immutable where appropriate
- Builder pattern for User creation

## Acceptance Criteria
- [ ] User entity class created
- [ ] Email value object with validation
- [ ] Password value object with hashing
- [ ] UserBuilder for flexible construction
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Implementation Notes
\`\`\`java
// Example structure
public class User {
    private final UserId id;
    private final Email email;
    private final Username username;
    private final HashedPassword password;
    // ... other fields
}
\`\`\`

## Testing Requirements
- Unit tests for User entity
- Email validation tests
- Password hashing tests
- Builder pattern tests" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Create Authentication Service Interface" \
    "## Description
Define the authentication service interface following ISP (Interface Segregation Principle).

## Requirements
- Clean interface for authentication operations
- Support for login, logout, token validation
- Strategy pattern for different auth methods
- No implementation details in interface

## Acceptance Criteria
- [ ] AuthenticationService interface defined
- [ ] AuthenticationStrategy interface for different auth types
- [ ] Clear method signatures with JavaDoc
- [ ] Exception types defined (AuthenticationException, etc.)
- [ ] 100% test coverage for implementations
- [ ] Follows engineering standards

## Interface Design
\`\`\`java
public interface AuthenticationService {
    AuthenticationResult authenticate(Credentials credentials);
    void logout(Token token);
    boolean validateToken(Token token);
}

public interface AuthenticationStrategy {
    boolean supports(Credentials credentials);
    AuthenticationResult execute(Credentials credentials);
}
\`\`\`" \
    "enhancement,priority-high,effort-small,engineering-standards"

create_issue \
    "Implement JWT Authentication Strategy" \
    "## Description
Implement JWT-based authentication strategy following the Strategy pattern.

## Requirements
- JWT token generation and validation
- Configurable token expiration
- Refresh token support
- Secure signing with HS256 or RS256

## Dependencies
- Add JWT library (e.g., jjwt, auth0 java-jwt)
- User domain model must be complete

## Acceptance Criteria
- [ ] JwtAuthenticationStrategy implements AuthenticationStrategy
- [ ] Token generation with claims (user id, roles, expiration)
- [ ] Token validation and parsing
- [ ] Refresh token mechanism
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Testing Requirements
- Token generation tests
- Token validation tests (valid, expired, invalid signature)
- Refresh token tests
- Edge cases (null, malformed tokens)" \
    "enhancement,priority-high,effort-large,code-quality"

create_issue \
    "Implement Password Hashing Service" \
    "## Description
Create a secure password hashing service using bcrypt or Argon2.

## Requirements
- Secure password hashing algorithm (bcrypt recommended)
- Salt generation
- Password verification
- Configurable work factor/cost

## Acceptance Criteria
- [ ] PasswordHasher interface defined
- [ ] BcryptPasswordHasher implementation
- [ ] Hash generation method
- [ ] Password verification method
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Security Requirements
- Use bcrypt with minimum cost factor of 12
- Never log or expose passwords
- Constant-time comparison for verification
- Handle edge cases (null, empty passwords)

## Testing Requirements
- Hash generation tests
- Password verification tests (correct, incorrect)
- Salt uniqueness tests
- Performance tests (ensure reasonable time)" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Create User Repository Interface and Implementation" \
    "## Description
Implement the User repository following Repository pattern and DIP (Dependency Inversion Principle).

## Requirements
- UserRepository interface for data access
- In-memory implementation for testing
- Support for CRUD operations
- Query by username, email, id

## Acceptance Criteria
- [ ] UserRepository interface defined
- [ ] InMemoryUserRepository implementation
- [ ] CRUD operations (create, read, update, delete)
- [ ] Query methods (findByUsername, findByEmail, findById)
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Implementation Notes
\`\`\`java
public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    User save(User user);
    void delete(UserId id);
}
\`\`\`

## Future Considerations
- Database implementation (JPA/JDBC)
- Transaction management
- Optimistic locking" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Implement User Registration Service" \
    "## Description
Create user registration service with validation and duplicate checking.

## Requirements
- Register new users with validation
- Check for duplicate username/email
- Hash passwords before storage
- Return appropriate errors for validation failures

## Dependencies
- User domain model
- UserRepository
- PasswordHasher

## Acceptance Criteria
- [ ] UserRegistrationService class created
- [ ] Registration method with validation
- [ ] Duplicate username/email checking
- [ ] Password hashing integration
- [ ] Appropriate exceptions for errors
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Validation Rules
- Username: 3-20 characters, alphanumeric + underscore
- Email: valid email format
- Password: minimum 8 characters, complexity requirements
- All fields required

## Testing Requirements
- Successful registration tests
- Duplicate username tests
- Duplicate email tests
- Invalid input tests (each validation rule)
- Password hashing verification" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Add Authentication Middleware/Filter" \
    "## Description
Create authentication middleware/filter for protecting endpoints (if using web framework).

## Requirements
- Intercept requests to protected endpoints
- Validate JWT token from Authorization header
- Extract user information from token
- Return 401 Unauthorized for invalid/missing tokens

## Acceptance Criteria
- [ ] AuthenticationFilter/Middleware created
- [ ] Token extraction from Authorization header
- [ ] Token validation using AuthenticationService
- [ ] User context population
- [ ] 401/403 error responses
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Implementation Notes
- Support Bearer token format: \`Authorization: Bearer <token>\`
- Handle missing token, expired token, invalid token
- Set user context for downstream handlers
- Exclude public endpoints from authentication" \
    "enhancement,priority-medium,effort-medium,code-quality"

create_issue \
    "Create Authentication REST API Endpoints" \
    "## Description
Implement REST API endpoints for authentication operations.

## Endpoints Required
- POST /api/auth/register - User registration
- POST /api/auth/login - User login
- POST /api/auth/logout - User logout
- POST /api/auth/refresh - Refresh access token
- GET /api/auth/me - Get current user info

## Acceptance Criteria
- [ ] All endpoints implemented
- [ ] Request/response DTOs defined
- [ ] Proper HTTP status codes (200, 201, 401, 400, etc.)
- [ ] Error handling with meaningful messages
- [ ] OpenAPI/Swagger documentation
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Request/Response Examples
\`\`\`json
// POST /api/auth/register
{
  \"username\": \"john_doe\",
  \"email\": \"john@example.com\",
  \"password\": \"SecurePass123!\"
}

// Response 201 Created
{
  \"id\": \"uuid\",
  \"username\": \"john_doe\",
  \"email\": \"john@example.com\"
}
\`\`\`

## Testing Requirements
- Integration tests for all endpoints
- Success scenarios
- Error scenarios (validation, auth failures)
- Edge cases" \
    "enhancement,priority-medium,effort-large,code-quality"

create_issue \
    "Add Authentication Configuration and Properties" \
    "## Description
Create configuration for authentication settings (JWT secret, token expiration, etc.).

## Requirements
- Externalized configuration (application.properties/yml)
- JWT secret key configuration
- Token expiration settings
- Password hashing configuration
- Environment-specific configs (dev, test, prod)

## Acceptance Criteria
- [ ] Configuration class created
- [ ] JWT settings (secret, expiration, issuer)
- [ ] Password hashing settings (algorithm, cost)
- [ ] Environment-specific property files
- [ ] Validation for required properties
- [ ] Documentation for all settings
- [ ] Follows engineering standards

## Configuration Properties
\`\`\`yaml
auth:
  jwt:
    secret: \${JWT_SECRET:change-me-in-production}
    access-token-expiration: 15m
    refresh-token-expiration: 7d
    issuer: liftit-java
  password:
    algorithm: bcrypt
    cost: 12
\`\`\`

## Security Notes
- Never commit secrets to version control
- Use environment variables for sensitive values
- Different secrets for different environments" \
    "enhancement,priority-medium,effort-small,code-quality"

create_issue \
    "Add Authentication Integration Tests" \
    "## Description
Create comprehensive integration tests for the authentication flow.

## Requirements
- End-to-end authentication flow tests
- Registration → Login → Protected endpoint access
- Token refresh flow
- Error scenarios

## Acceptance Criteria
- [ ] Integration test suite created
- [ ] Full registration flow test
- [ ] Full login flow test
- [ ] Token validation test
- [ ] Token refresh test
- [ ] Logout test
- [ ] Protected endpoint access test
- [ ] 80%+ coverage for auth module
- [ ] Follows engineering standards

## Test Scenarios
1. **Happy Path**: Register → Login → Access protected resource
2. **Token Expiration**: Login → Wait → Access with expired token → Refresh → Access
3. **Invalid Credentials**: Login with wrong password
4. **Duplicate Registration**: Register same user twice
5. **Unauthorized Access**: Access protected resource without token

## Testing Tools
- JUnit 5 for test framework
- AssertJ for assertions
- Test containers (if using database)
- MockMvc or REST Assured for API testing" \
    "test,priority-high,effort-large,code-quality"

create_issue \
    "Add Authentication Documentation" \
    "## Description
Create comprehensive documentation for the authentication system.

## Requirements
- Architecture overview
- API documentation
- Setup and configuration guide
- Security best practices
- Code examples

## Acceptance Criteria
- [ ] Authentication architecture documented
- [ ] API endpoints documented (OpenAPI/Swagger)
- [ ] Configuration guide created
- [ ] Security considerations documented
- [ ] Code examples for common use cases
- [ ] Troubleshooting guide
- [ ] Follows engineering standards

## Documentation Sections
1. **Overview**: Authentication architecture and flow
2. **Getting Started**: Setup and configuration
3. **API Reference**: All endpoints with examples
4. **Security**: Best practices and considerations
5. **Examples**: Common integration patterns
6. **Troubleshooting**: Common issues and solutions

## Deliverables
- [ ] docs/authentication/README.md
- [ ] docs/authentication/architecture.md
- [ ] docs/authentication/api-reference.md
- [ ] docs/authentication/security.md
- [ ] OpenAPI/Swagger spec" \
    "documentation,priority-medium,effort-medium"

echo ""
echo "✅ All authentication issues created successfully!"
echo "View issues at: https://github.com/$REPO/issues"
echo ""
echo "Summary: 12 issues created"
echo "  - Foundation: 1 (architecture design)"
echo "  - Domain Model: 1 (User model)"
echo "  - Core Services: 5 (auth service, JWT, password, repository, registration)"
echo "  - API Layer: 2 (middleware, REST endpoints)"
echo "  - Configuration: 1"
echo "  - Testing: 1 (integration tests)"
echo "  - Documentation: 1"
