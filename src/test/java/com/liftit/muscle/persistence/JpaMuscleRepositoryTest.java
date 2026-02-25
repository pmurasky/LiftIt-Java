package com.liftit.muscle.persistence;

import com.liftit.muscle.Muscle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaMuscleRepositoryTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Mock
    private MuscleJpaRepository springDataRepository;

    private JpaMuscleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaMuscleRepository(springDataRepository);
    }

    @Test
    void shouldReturnAllMusclesAsDomainRecords() {
        // Given
        Muscle muscle1 = new Muscle(1L, "Abdominals", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        Muscle muscle2 = new Muscle(2L, "Back", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        List<MuscleJpaEntity> entities = List.of(
                MuscleJpaEntity.fromDomain(muscle1),
                MuscleJpaEntity.fromDomain(muscle2)
        );
        when(springDataRepository.findAll()).thenReturn(entities);

        // When
        List<Muscle> result = repository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Abdominals", result.get(0).name());
        assertEquals(2L, result.get(1).id());
        assertEquals("Back", result.get(1).name());
        verify(springDataRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoMusclesExist() {
        // Given
        when(springDataRepository.findAll()).thenReturn(List.of());

        // When
        List<Muscle> result = repository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(springDataRepository).findAll();
    }
}
