package com.gym.crm.service;

import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.dao.impl.TrainerDaoImpl;
import com.gym.crm.dao.impl.TrainingDaoImpl;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDaoImpl trainingDaoImpl;

    @Mock
    private TraineeDaoImpl traineeDaoImpl;

    @Mock
    private TrainerDaoImpl trainerDaoImpl;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void createTraining_Success() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");
        training.setTrainingType(new TrainingType());
        training.setTrainerId(1L);
        training.setTraineeId(2L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        Trainee trainee = new Trainee();
        trainee.setId(2L);

        when(trainerDaoImpl.findById(1L)).thenReturn(trainer);
        when(traineeDaoImpl.findById(2L)).thenReturn(trainee);

        trainingService.createTraining(training);

        verify(trainingDaoImpl).save(training);
    }

    @Test
    void createTraining_NullTraining_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(null)
        );
        assertEquals("Training must not be null", exception.getMessage());
    }

    @Test
    void createTraining_BlankName_ThrowsException() {
        Training training = new Training();
        training.setTrainingName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(training)
        );
        assertEquals("Training name must not be blank", exception.getMessage());
    }

    @Test
    void createTraining_NullType_ThrowsException() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");
        training.setTrainingType(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(training)
        );
        assertEquals("Training type must not be null", exception.getMessage());
    }

    @Test
    void createTraining_NullTrainerId_ThrowsException() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");
        training.setTrainingType(new TrainingType());
        training.setTrainerId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(training)
        );
        assertEquals("TrainerId must not be null", exception.getMessage());
    }

    @Test
    void createTraining_NullTraineeId_ThrowsException() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");
        training.setTrainingType(new TrainingType());
        training.setTrainerId(1L);
        training.setTraineeId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(training)
        );
        assertEquals("TraineeId must not be null", exception.getMessage());
    }

    @Test
    void createTraining_TrainerNotFound_ThrowsException() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");
        training.setTrainingType(new TrainingType());
        training.setTrainerId(1L);
        training.setTraineeId(2L);

        when(trainerDaoImpl.findById(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(training)
        );
        assertEquals("Trainer with id=1 does not exist", exception.getMessage());
    }

    @Test
    void createTraining_TraineeNotFound_ThrowsException() {
        Training training = new Training();
        training.setTrainingName("Fitness Training");
        training.setTrainingType(new TrainingType());
        training.setTrainerId(1L);
        training.setTraineeId(2L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(trainerDaoImpl.findById(1L)).thenReturn(trainer);
        when(traineeDaoImpl.findById(2L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(training)
        );
        assertEquals("Trainee with id=2 does not exist", exception.getMessage());
    }

    @Test
    void selectTraining_Success() {
        Long id = 1L;
        Training expectedTraining = new Training();
        expectedTraining.setId(id);
        when(trainingDaoImpl.findById(id)).thenReturn(expectedTraining);

        Training result = trainingService.selectTraining(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(trainingDaoImpl).findById(id);
    }

    @Test
    void getAllTrainings_Success() {
        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        List<Training> expectedTrainings = Arrays.asList(training1, training2);

        when(trainingDaoImpl.findAll()).thenReturn(expectedTrainings);

        List<Training> result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingDaoImpl).findAll();
    }
}

