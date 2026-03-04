package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import com.gym.crm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_Success() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");

        trainingService.createTraining(training);

        verify(trainingDao).save(training);
    }

    @Test
    void selectTraining_Success() {
        Long id = 1L;
        Training expectedTraining = new Training();
        expectedTraining.setId(id);
        when(trainingDao.findById(id)).thenReturn(expectedTraining);

        Optional<Training> result = trainingService.selectTraining(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(trainingDao).findById(id);
    }

    @Test
    void getAllTrainings_Success() {
        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        List<Training> expectedTrainings = Arrays.asList(training1, training2);

        when(trainingDao.findAll()).thenReturn(expectedTrainings);

        List<Training> result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingDao).findAll();
    }

    @Test
    void selectTraining_NotFound() {
        Long id = 999L;
        when(trainingDao.findById(id)).thenReturn(null);

        Optional<Training> result = trainingService.selectTraining(id);

        assertFalse(result.isPresent());
        verify(trainingDao).findById(id);
    }

    @Test
    void getAllTrainings_Empty() {
        when(trainingDao.findAll()).thenReturn(Arrays.asList());

        List<Training> result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(trainingDao).findAll();
    }

    @Test
    void createTraining_VerifiesTrainingIsSaved() {
        Training training = new Training();
        training.setTrainingName("Advanced Fitness");

        trainingService.createTraining(training);

        verify(trainingDao).save(training);
    }
}
