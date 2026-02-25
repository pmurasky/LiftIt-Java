package com.liftit.workout.persistence;

import com.liftit.workout.Weight;
import com.liftit.workout.WeightUnit;
import com.liftit.workout.WorkoutSet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * JPA entity mapping to the {@code workout_sets} table.
 *
 * <p>Owned by {@link WorkoutExerciseJpaEntity}. Converts to/from
 * the {@link WorkoutSet} domain value object via {@link #toDomain()}
 * and {@link #fromDomain(WorkoutSet, WorkoutExerciseJpaEntity)}.
 */
@Entity
@Table(name = "workout_sets")
class WorkoutSetJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    private WorkoutExerciseJpaEntity workoutExercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal weightValue;

    @Column(name = "weight_unit", nullable = false, length = 10)
    private String weightUnit;

    @Column(name = "rpe")
    private Integer rpe;

    /** Required by JPA. */
    protected WorkoutSetJpaEntity() {
    }

    private WorkoutSetJpaEntity(
            WorkoutExerciseJpaEntity workoutExercise,
            Integer setNumber,
            Integer reps,
            BigDecimal weightValue,
            String weightUnit,
            Integer rpe) {
        this.workoutExercise = workoutExercise;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weightValue = weightValue;
        this.weightUnit = weightUnit;
        this.rpe = rpe;
    }

    /**
     * Converts this JPA entity to a {@link WorkoutSet} domain value object.
     *
     * @return the domain value object
     */
    WorkoutSet toDomain() {
        WeightUnit unit = WeightUnit.valueOf(weightUnit);
        Weight weight = new Weight(weightValue.doubleValue(), unit);
        return new WorkoutSet(setNumber, reps, weight, rpe);
    }

    /**
     * Creates a {@code WorkoutSetJpaEntity} from a {@link WorkoutSet} domain value object.
     *
     * @param set             the domain value object; must not be null
     * @param workoutExercise the owning exercise entity; must not be null
     * @return a new entity ready for persistence
     */
    static WorkoutSetJpaEntity fromDomain(WorkoutSet set, WorkoutExerciseJpaEntity workoutExercise) {
        BigDecimal weightValue = BigDecimal.valueOf(set.weight().value());
        String weightUnit = set.weight().unit().name();
        return new WorkoutSetJpaEntity(workoutExercise, set.setNumber(), set.reps(),
                weightValue, weightUnit, set.rpe());
    }
}
