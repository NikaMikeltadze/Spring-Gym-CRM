package com.gym.crm.dao.impl;

import com.gym.crm.entity.Training;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {

    @Mock
    private EntityManager entityManager;

    private TrainingDaoImpl trainingDao;

    @BeforeEach
    void setUp() {
        trainingDao = new TrainingDaoImpl();
        trainingDao.setEntityManager(entityManager);
    }

    @Test
    void save_Success() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Fitness Training");

        trainingDao.save(training);

        verify(entityManager).persist(training);
    }

    @Test
    void findById_Success() {
        Long id = 1L;
        Training expectedTraining = new Training();
        expectedTraining.setId(id);
        expectedTraining.setTrainingName("Fitness Training");

        when(entityManager.find(Training.class, id)).thenReturn(expectedTraining);

        Training result = trainingDao.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Fitness Training", result.getTrainingName());
        verify(entityManager).find(Training.class, id);
    }

    @Test
    void findById_NotFound() {
        Long id = 999L;
        when(entityManager.find(Training.class, id)).thenReturn(null);

        Training result = trainingDao.findById(id);

        assertNull(result);
        verify(entityManager).find(Training.class, id);
    }
}
