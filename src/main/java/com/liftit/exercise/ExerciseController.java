package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import com.liftit.user.Auth0Id;
import com.liftit.user.User;
import com.liftit.user.UserRepository;
import com.liftit.user.exception.UnauthorizedException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for exercise management.
 *
 * <h3>Identity resolution</h3>
 * <p>Mutating endpoints identify the caller from the JWT {@code sub} claim, which the
 * {@link com.liftit.auth.AuthenticationFilter} extracts and stores as the principal in
 * {@link SecurityContextHolder}. User identity is <em>never</em> accepted from request
 * headers or the request body — callers cannot supply their own {@code userId}
 * (IDOR prevention).
 *
 * <h3>Authorization</h3>
 * <p>All endpoints require authentication. Update and delete operations additionally
 * require that the authenticated user is the creator of the exercise.
 */
@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final UserRepository userRepository;

    public ExerciseController(ExerciseService exerciseService, UserRepository userRepository) {
        this.exerciseService = exerciseService;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new exercise owned by the authenticated user.
     *
     * @param request the exercise details
     * @return {@code 201 Created} with the created exercise
     */
    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody CreateExerciseRequest request) {
        Long userId = resolveUserId();
        Exercise exercise = exerciseService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciseResponse.from(exercise));
    }

    /**
     * Returns a single exercise by ID.
     *
     * @param id the exercise ID
     * @return {@code 200 OK} with the exercise, or {@code 404 Not Found}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) {
        Exercise exercise = exerciseService.getById(id);
        return ResponseEntity.ok(ExerciseResponse.from(exercise));
    }

    /**
     * Fully replaces an existing exercise. Only the owner may update.
     *
     * @param id      the exercise ID
     * @param request the replacement fields
     * @return {@code 200 OK} with the updated exercise
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExerciseRequest request) {
        Long userId = resolveUserId();
        Exercise updated = exerciseService.update(id, request, userId);
        return ResponseEntity.ok(ExerciseResponse.from(updated));
    }

    /**
     * Deletes an exercise. Only the owner may delete.
     *
     * @param id the exercise ID
     * @return {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = resolveUserId();
        exerciseService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns a paginated, filtered list of exercises.
     *
     * @param category    optional category filter
     * @param muscleGroup optional muscle group filter
     * @param search      optional name substring search
     * @param page        zero-based page number (default 0)
     * @param size        page size (default 20)
     * @return {@code 200 OK} with the page of exercises
     */
    @GetMapping
    public ResponseEntity<Page<ExerciseResponse>> list(
            @RequestParam(required = false) ExerciseCategoryEnum category,
            @RequestParam(required = false) MuscleEnum muscleGroup,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ExerciseFilter filter = new ExerciseFilter(category, muscleGroup, search);
        Page<ExerciseResponse> result = exerciseService
                .list(filter, PageRequest.of(page, size))
                .map(ExerciseResponse::from);
        return ResponseEntity.ok(result);
    }

    /**
     * Returns all available exercise categories.
     *
     * @return {@code 200 OK} with the list of categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<ExerciseCategory>> getCategories() {
        return ResponseEntity.ok(exerciseService.getCategories());
    }

    /**
     * Returns all available muscle groups.
     *
     * @return {@code 200 OK} with the list of muscle groups
     */
    @GetMapping("/muscle-groups")
    public ResponseEntity<List<MuscleEnum>> getMuscleGroups() {
        return ResponseEntity.ok(exerciseService.getMuscleGroups());
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
}
