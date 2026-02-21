#!/bin/bash
# Script to create workout tracking domain issues
# Requires: GitHub CLI (gh) installed and authenticated
# Usage: ./create-workout-domain-issues.sh

set -e

REPO="pmurasky/LiftIt-Java"

echo "🏋️ Creating workout tracking domain issues for $REPO"
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

echo "Creating workout domain epic..."

create_issue \
    "[EPIC] Implement Workout Tracking Domain" \
    "## Overview

This epic tracks the implementation of the core workout tracking domain for LiftIt - a personal fitness tracking system.

## Domain Model

LiftIt allows authenticated users to:
- Log structured workout sessions
- Track exercises and sets
- Monitor progress over time
- Maintain workout history
- Manage personal workout data securely

## Core Entities

- **User** - Authenticated user (from Auth epic #14)
- **Workout** - A workout session (date, duration, notes)
- **Exercise** - A type of exercise (name, category, muscle groups)
- **WorkoutExercise** - An exercise performed in a workout
- **Set** - A set within an exercise (reps, weight, rest time)

## Architecture Principles

- **Domain-Driven Design (DDD)** - Rich domain model
- **SOLID Principles** throughout
- **Repository Pattern** for data access
- **Value Objects** for type safety
- **Aggregate Roots** for consistency boundaries
- **TDD Micro-Commit Workflow**

## Implementation Issues

### Domain Model
- [ ] #TBD Design Workout Domain Model
- [ ] #TBD Implement Exercise Entity
- [ ] #TBD Implement Workout Entity
- [ ] #TBD Implement Set Value Object
- [ ] #TBD Implement WorkoutExercise Entity

### Repositories
- [ ] #TBD Create Exercise Repository
- [ ] #TBD Create Workout Repository

### Services
- [ ] #TBD Implement Workout Service
- [ ] #TBD Implement Exercise Service
- [ ] #TBD Implement Progress Tracking Service

### API Layer
- [ ] #TBD Create Workout REST API Endpoints
- [ ] #TBD Create Exercise REST API Endpoints

### Data Persistence
- [ ] #TBD Design Database Schema
- [ ] #TBD Set Up Database Migrations (Flyway)
- [ ] #TBD Implement JPA Entities

### Testing & Documentation
- [ ] #TBD Add Workout Domain Integration Tests
- [ ] #TBD Add Workout API Documentation

## Success Criteria

- [ ] All sub-issues completed
- [ ] 80%+ test coverage for domain module
- [ ] All endpoints documented (OpenAPI/Swagger)
- [ ] Code follows engineering standards
- [ ] Integration tests passing
- [ ] Database migrations working

## Dependencies

- Authentication system (#14)
- Database (PostgreSQL recommended)
- JPA/Hibernate
- Flyway for migrations
- REST framework (Spring Boot or Javalin)

## Timeline Estimate

- **Effort**: Large (3-4 weeks)
- **Priority**: High
- **Complexity**: High" \
    "enhancement,priority-high,effort-large"

echo ""
echo "Creating domain model issues..."

create_issue \
    "Design Workout Domain Model" \
    "## Description
Design the complete domain model for workout tracking following DDD principles and SOLID design.

## Requirements
- Define core entities and their relationships
- Identify value objects for type safety
- Define aggregate roots and boundaries
- Document domain invariants and business rules
- Create domain model diagram

## Core Entities

### Workout (Aggregate Root)
- WorkoutId (value object)
- UserId (foreign key to User)
- Date/Time
- Duration
- Notes
- List<WorkoutExercise>

### Exercise (Aggregate Root)
- ExerciseId (value object)
- Name
- Category (enum: STRENGTH, CARDIO, FLEXIBILITY)
- MuscleGroups (list of enums)
- Description
- CreatedBy (UserId)

### WorkoutExercise (Entity within Workout aggregate)
- WorkoutExerciseId
- ExerciseId (reference)
- Order (sequence in workout)
- List<Set>
- Notes

### Set (Value Object)
- SetNumber
- Reps
- Weight (with unit: lbs/kg)
- RestTime (duration)
- RPE (Rate of Perceived Exertion, 1-10)

## Value Objects
- WorkoutId
- ExerciseId
- WorkoutExerciseId
- Weight (value + unit)
- Duration
- MuscleGroup (enum)
- ExerciseCategory (enum)

## Acceptance Criteria
- [ ] Domain model diagram created
- [ ] All entities and value objects defined
- [ ] Aggregate boundaries identified
- [ ] Business rules documented
- [ ] Relationships and cardinalities defined
- [ ] Reviewed and approved

## Deliverables
- Domain model diagram (UML or similar)
- Documentation in docs/domain/workout-model.md
- Business rules document" \
    "enhancement,priority-high,effort-medium,documentation"

create_issue \
    "Implement Exercise Entity" \
    "## Description
Implement the Exercise entity following DDD and SOLID principles.

## Requirements
- Exercise aggregate root
- ExerciseId value object
- Exercise categories and muscle groups
- Builder pattern for construction
- Immutability where appropriate

## Domain Model
\`\`\`java
public class Exercise {
    private final ExerciseId id;
    private final String name;
    private final ExerciseCategory category;
    private final Set<MuscleGroup> muscleGroups;
    private final String description;
    private final UserId createdBy;
    private final Instant createdAt;
    
    // Business methods
    public boolean targets(MuscleGroup muscleGroup);
    public boolean isStrengthExercise();
    // ...
}

public enum ExerciseCategory {
    STRENGTH, CARDIO, FLEXIBILITY, PLYOMETRIC
}

public enum MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, 
    LEGS, QUADS, HAMSTRINGS, GLUTES, CALVES,
    CORE, ABS, OBLIQUES
}
\`\`\`

## Acceptance Criteria
- [ ] Exercise class created
- [ ] ExerciseId value object implemented
- [ ] ExerciseCategory enum defined
- [ ] MuscleGroup enum defined
- [ ] Builder pattern implemented
- [ ] Business methods added
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Testing Requirements
- Unit tests for Exercise creation
- Validation tests (name, category, muscle groups)
- Business logic tests
- Builder pattern tests
- Edge cases (null, empty values)" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Implement Workout Entity" \
    "## Description
Implement the Workout aggregate root with WorkoutExercise entities and Set value objects.

## Requirements
- Workout aggregate root
- WorkoutId value object
- WorkoutExercise entity
- Set value object
- Aggregate consistency enforcement

## Domain Model
\`\`\`java
public class Workout {
    private final WorkoutId id;
    private final UserId userId;
    private final Instant startTime;
    private Duration duration;
    private String notes;
    private final List<WorkoutExercise> exercises;
    
    // Business methods
    public void addExercise(Exercise exercise);
    public void removeExercise(WorkoutExerciseId id);
    public void completeWorkout();
    public int getTotalSets();
    public Duration getTotalRestTime();
    // ...
}

public class WorkoutExercise {
    private final WorkoutExerciseId id;
    private final ExerciseId exerciseId;
    private final int order;
    private final List<Set> sets;
    private String notes;
    
    public void addSet(Set set);
    public void removeSet(int setNumber);
    // ...
}

public class Set {
    private final int setNumber;
    private final int reps;
    private final Weight weight;
    private final Duration restTime;
    private final Integer rpe; // 1-10, optional
    
    // Value object - immutable
}

public class Weight {
    private final double value;
    private final WeightUnit unit;
    
    public Weight convertTo(WeightUnit targetUnit);
}
\`\`\`

## Acceptance Criteria
- [ ] Workout aggregate root created
- [ ] WorkoutId value object implemented
- [ ] WorkoutExercise entity created
- [ ] Set value object implemented
- [ ] Weight value object with unit conversion
- [ ] Aggregate consistency rules enforced
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Testing Requirements
- Workout creation and modification tests
- Exercise addition/removal tests
- Set tracking tests
- Weight conversion tests
- Aggregate boundary tests
- Business rule validation tests" \
    "enhancement,priority-high,effort-large,code-quality"

create_issue \
    "Create Exercise Repository" \
    "## Description
Implement the Exercise repository following Repository pattern and DIP.

## Requirements
- ExerciseRepository interface
- In-memory implementation for testing
- JPA implementation for production
- Query methods for finding exercises

## Repository Interface
\`\`\`java
public interface ExerciseRepository {
    Optional<Exercise> findById(ExerciseId id);
    List<Exercise> findByCategory(ExerciseCategory category);
    List<Exercise> findByMuscleGroup(MuscleGroup muscleGroup);
    List<Exercise> findByUserId(UserId userId);
    List<Exercise> findAll();
    Exercise save(Exercise exercise);
    void delete(ExerciseId id);
}
\`\`\`

## Acceptance Criteria
- [ ] ExerciseRepository interface defined
- [ ] InMemoryExerciseRepository implementation
- [ ] JpaExerciseRepository implementation
- [ ] All query methods implemented
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Testing Requirements
- CRUD operation tests
- Query method tests
- Edge cases (not found, duplicates)
- Both in-memory and JPA implementations tested" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Create Workout Repository" \
    "## Description
Implement the Workout repository following Repository pattern and DIP.

## Requirements
- WorkoutRepository interface
- In-memory implementation for testing
- JPA implementation for production
- Query methods for workout history and analytics

## Repository Interface
\`\`\`java
public interface WorkoutRepository {
    Optional<Workout> findById(WorkoutId id);
    List<Workout> findByUserId(UserId userId);
    List<Workout> findByUserIdAndDateRange(UserId userId, LocalDate start, LocalDate end);
    List<Workout> findByUserIdAndExercise(UserId userId, ExerciseId exerciseId);
    Workout save(Workout workout);
    void delete(WorkoutId id);
    
    // Analytics queries
    long countByUserId(UserId userId);
    Optional<Workout> findMostRecentByUserId(UserId userId);
}
\`\`\`

## Acceptance Criteria
- [ ] WorkoutRepository interface defined
- [ ] InMemoryWorkoutRepository implementation
- [ ] JpaWorkoutRepository implementation
- [ ] All query methods implemented
- [ ] Date range queries working
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Testing Requirements
- CRUD operation tests
- Query method tests (by user, date range, exercise)
- Analytics query tests
- Edge cases (empty results, invalid dates)
- Both in-memory and JPA implementations tested" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Implement Workout Service" \
    "## Description
Create the Workout service for business logic and orchestration.

## Requirements
- Workout creation and management
- Exercise addition to workouts
- Set logging
- Workout completion
- Business rule enforcement

## Service Interface
\`\`\`java
public interface WorkoutService {
    Workout startWorkout(UserId userId, Instant startTime);
    Workout addExerciseToWorkout(WorkoutId workoutId, ExerciseId exerciseId);
    Workout logSet(WorkoutId workoutId, WorkoutExerciseId exerciseId, Set set);
    Workout completeWorkout(WorkoutId workoutId);
    void deleteWorkout(WorkoutId workoutId);
    Optional<Workout> getWorkout(WorkoutId workoutId);
    List<Workout> getUserWorkouts(UserId userId);
    List<Workout> getWorkoutHistory(UserId userId, LocalDate start, LocalDate end);
}
\`\`\`

## Acceptance Criteria
- [ ] WorkoutService interface defined
- [ ] Implementation created
- [ ] Business rules enforced (user owns workout, etc.)
- [ ] Transaction management
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Business Rules
- User can only modify their own workouts
- Cannot add exercises to completed workout
- Cannot delete workout with logged sets (soft delete)
- Workout duration calculated automatically

## Testing Requirements
- Workout lifecycle tests (start → add exercises → log sets → complete)
- Authorization tests (user ownership)
- Business rule validation tests
- Error scenarios" \
    "enhancement,priority-high,effort-large,code-quality"

create_issue \
    "Implement Progress Tracking Service" \
    "## Description
Create service for tracking workout progress and analytics.

## Requirements
- Personal records (PRs) tracking
- Volume calculations
- Progress over time
- Exercise-specific analytics

## Service Interface
\`\`\`java
public interface ProgressTrackingService {
    Optional<PersonalRecord> getPersonalRecord(UserId userId, ExerciseId exerciseId);
    Map<ExerciseId, PersonalRecord> getAllPersonalRecords(UserId userId);
    WorkoutStats getWorkoutStats(UserId userId, LocalDate start, LocalDate end);
    ExerciseProgress getExerciseProgress(UserId userId, ExerciseId exerciseId, LocalDate start, LocalDate end);
    
    // Volume = sets × reps × weight
    double calculateTotalVolume(Workout workout);
    double calculateExerciseVolume(WorkoutExercise exercise);
}

public class PersonalRecord {
    private final ExerciseId exerciseId;
    private final Weight maxWeight;
    private final int maxReps;
    private final Instant achievedAt;
}

public class WorkoutStats {
    private final int totalWorkouts;
    private final Duration totalDuration;
    private final int totalSets;
    private final double totalVolume;
}
\`\`\`

## Acceptance Criteria
- [ ] ProgressTrackingService interface defined
- [ ] Implementation created
- [ ] PR tracking working
- [ ] Volume calculations correct
- [ ] Stats aggregation working
- [ ] 100% test coverage
- [ ] All methods ≤ 15 lines
- [ ] Follows engineering standards

## Testing Requirements
- PR calculation tests
- Volume calculation tests
- Stats aggregation tests
- Edge cases (no workouts, no PRs)" \
    "enhancement,priority-medium,effort-large,code-quality"

create_issue \
    "Create Workout REST API Endpoints" \
    "## Description
Implement REST API endpoints for workout management.

## Endpoints Required
- POST /api/workouts - Start new workout
- GET /api/workouts/{id} - Get workout details
- PUT /api/workouts/{id} - Update workout
- DELETE /api/workouts/{id} - Delete workout
- POST /api/workouts/{id}/complete - Complete workout
- GET /api/workouts - List user's workouts (with pagination)
- GET /api/workouts/history - Get workout history (date range)

- POST /api/workouts/{id}/exercises - Add exercise to workout
- DELETE /api/workouts/{id}/exercises/{exerciseId} - Remove exercise
- POST /api/workouts/{id}/exercises/{exerciseId}/sets - Log a set

## Acceptance Criteria
- [ ] All endpoints implemented
- [ ] Request/response DTOs defined
- [ ] Proper HTTP status codes
- [ ] Error handling with meaningful messages
- [ ] Authentication required (JWT)
- [ ] Authorization (user owns workout)
- [ ] Pagination for list endpoints
- [ ] OpenAPI/Swagger documentation
- [ ] 100% test coverage
- [ ] Follows engineering standards

## Request/Response Examples
\`\`\`json
// POST /api/workouts
{
  \"startTime\": \"2026-02-16T20:00:00Z\",
  \"notes\": \"Chest day\"
}

// Response 201 Created
{
  \"id\": \"workout-uuid\",
  \"userId\": \"user-uuid\",
  \"startTime\": \"2026-02-16T20:00:00Z\",
  \"duration\": null,
  \"notes\": \"Chest day\",
  \"exercises\": [],
  \"completed\": false
}
\`\`\`

## Testing Requirements
- Integration tests for all endpoints
- Success scenarios
- Error scenarios (validation, auth, not found)
- Pagination tests" \
    "enhancement,priority-high,effort-large,code-quality"

create_issue \
    "Create Exercise REST API Endpoints" \
    "## Description
Implement REST API endpoints for exercise management.

## Endpoints Required
- POST /api/exercises - Create custom exercise
- GET /api/exercises/{id} - Get exercise details
- PUT /api/exercises/{id} - Update exercise
- DELETE /api/exercises/{id} - Delete exercise
- GET /api/exercises - List exercises (with filters)
- GET /api/exercises/categories - List categories
- GET /api/exercises/muscle-groups - List muscle groups

## Query Parameters
- category - Filter by category
- muscleGroup - Filter by muscle group
- search - Search by name
- page, size - Pagination

## Acceptance Criteria
- [ ] All endpoints implemented
- [ ] Request/response DTOs defined
- [ ] Filtering and search working
- [ ] Proper HTTP status codes
- [ ] Error handling
- [ ] Authentication required
- [ ] Authorization (user can only modify their exercises)
- [ ] Pagination
- [ ] OpenAPI/Swagger documentation
- [ ] 100% test coverage
- [ ] Follows engineering standards

## Request/Response Examples
\`\`\`json
// POST /api/exercises
{
  \"name\": \"Barbell Bench Press\",
  \"category\": \"STRENGTH\",
  \"muscleGroups\": [\"CHEST\", \"TRICEPS\", \"SHOULDERS\"],
  \"description\": \"Compound chest exercise\"
}

// Response 201 Created
{
  \"id\": \"exercise-uuid\",
  \"name\": \"Barbell Bench Press\",
  \"category\": \"STRENGTH\",
  \"muscleGroups\": [\"CHEST\", \"TRICEPS\", \"SHOULDERS\"],
  \"description\": \"Compound chest exercise\",
  \"createdBy\": \"user-uuid\",
  \"createdAt\": \"2026-02-16T20:00:00Z\"
}
\`\`\`" \
    "enhancement,priority-high,effort-medium,code-quality"

create_issue \
    "Design Database Schema for Workout Domain" \
    "## Description
Design the database schema for workout tracking following normalization principles.

## Requirements
- Tables for all domain entities
- Proper relationships and foreign keys
- Indexes for query performance
- Constraints for data integrity

## Tables

### exercises
- id (UUID, PK)
- name (VARCHAR, NOT NULL)
- category (VARCHAR, NOT NULL)
- description (TEXT)
- created_by (UUID, FK to users)
- created_at (TIMESTAMP)

### exercise_muscle_groups
- exercise_id (UUID, FK)
- muscle_group (VARCHAR)
- PRIMARY KEY (exercise_id, muscle_group)

### workouts
- id (UUID, PK)
- user_id (UUID, FK to users, NOT NULL)
- start_time (TIMESTAMP, NOT NULL)
- duration (INTERVAL)
- notes (TEXT)
- completed (BOOLEAN, DEFAULT false)
- created_at (TIMESTAMP)

### workout_exercises
- id (UUID, PK)
- workout_id (UUID, FK to workouts, NOT NULL)
- exercise_id (UUID, FK to exercises, NOT NULL)
- order_index (INT, NOT NULL)
- notes (TEXT)

### sets
- id (UUID, PK)
- workout_exercise_id (UUID, FK to workout_exercises, NOT NULL)
- set_number (INT, NOT NULL)
- reps (INT, NOT NULL)
- weight_value (DECIMAL)
- weight_unit (VARCHAR)
- rest_time_seconds (INT)
- rpe (INT, CHECK rpe BETWEEN 1 AND 10)

## Indexes
- workouts(user_id, start_time)
- workout_exercises(workout_id)
- sets(workout_exercise_id)

## Acceptance Criteria
- [ ] Schema diagram created
- [ ] All tables defined
- [ ] Relationships documented
- [ ] Indexes identified
- [ ] Constraints defined
- [ ] Migration scripts ready
- [ ] Reviewed and approved" \
    "enhancement,priority-high,effort-medium,documentation"

create_issue \
    "Set Up Database Migrations with Flyway" \
    "## Description
Set up Flyway for database schema migrations.

## Requirements
- Flyway dependency added
- Migration scripts for workout schema
- Baseline migration
- Version control for schema changes

## Acceptance Criteria
- [ ] Flyway added to build.gradle
- [ ] Migration directory structure created
- [ ] Initial migration (V1__create_workout_schema.sql)
- [ ] Migrations run successfully
- [ ] Rollback scripts created
- [ ] Documentation for adding migrations
- [ ] Follows engineering standards

## Migration Files
- V1__create_users_table.sql (from auth)
- V2__create_exercises_table.sql
- V3__create_workouts_table.sql
- V4__create_workout_exercises_table.sql
- V5__create_sets_table.sql
- V6__add_indexes.sql

## Testing Requirements
- Migration execution tests
- Rollback tests
- Schema validation tests" \
    "enhancement,priority-high,effort-small,code-quality"

create_issue \
    "Implement JPA Entities for Workout Domain" \
    "## Description
Create JPA entity mappings for the workout domain model.

## Requirements
- JPA entities for all domain objects
- Proper relationships (@OneToMany, @ManyToOne)
- Cascade operations
- Fetch strategies
- Converters for value objects

## Acceptance Criteria
- [ ] Exercise JPA entity created
- [ ] Workout JPA entity created
- [ ] WorkoutExercise JPA entity created
- [ ] Set embeddable created
- [ ] Attribute converters for value objects
- [ ] Relationships properly mapped
- [ ] Cascade operations configured
- [ ] 100% test coverage
- [ ] Follows engineering standards

## Implementation Notes
\`\`\`java
@Entity
@Table(name = \"workouts\")
public class WorkoutJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = \"user_id\", nullable = false)
    private UUID userId;
    
    @OneToMany(mappedBy = \"workout\", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExerciseJpaEntity> exercises;
    
    // Map to domain model
    public Workout toDomain();
    public static WorkoutJpaEntity fromDomain(Workout workout);
}
\`\`\`

## Testing Requirements
- Entity mapping tests
- Relationship tests
- Cascade operation tests
- Domain conversion tests" \
    "enhancement,priority-high,effort-large,code-quality"

echo ""
echo "✅ All workout domain issues created successfully!"
echo "View issues at: https://github.com/$REPO/issues"
echo ""
echo "Summary: 13 issues created"
echo "  - Epic: 1 (tracking issue)"
echo "  - Domain Model: 3 (design, Exercise, Workout)"
echo "  - Repositories: 2 (Exercise, Workout)"
echo "  - Services: 2 (Workout, Progress Tracking)"
echo "  - API Layer: 2 (Workout endpoints, Exercise endpoints)"
echo "  - Data Persistence: 3 (schema design, Flyway, JPA entities)"
