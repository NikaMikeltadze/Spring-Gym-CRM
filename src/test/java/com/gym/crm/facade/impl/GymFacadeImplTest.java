package com.gym.crm.facade.impl;

import com.gym.crm.dto.TraineeDTO;
import com.gym.crm.dto.TrainerDTO;
import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeImplTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GymFacadeImpl gymFacade;

    @Test
    void createTrainee_Success() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setId(1L);
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");

        Trainee traineeEntity = new Trainee();
        traineeEntity.setId(1L);
        traineeEntity.setFirstName("John");
        traineeEntity.setLastName("Doe");

        when(modelMapper.map(traineeDTO, Trainee.class)).thenReturn(traineeEntity);
        gymFacade.createTrainee(traineeDTO);

        verify(modelMapper).map(traineeDTO, Trainee.class);
        verify(traineeService).createTrainee(traineeEntity);
    }

    @Test
    void getTraineeByUsername_Found() {
        String username = "John.Doe";
        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setFirstName("John");
        traineeEntity.setLastName("Doe");

        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setUsername(username);
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");

        when(traineeService.selectTraineeByUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(modelMapper.map(traineeEntity, TraineeDTO.class)).thenReturn(traineeDTO);

        Optional<TraineeDTO> result = gymFacade.getTraineeByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(traineeService).selectTraineeByUsername(username);
        verify(modelMapper).map(traineeEntity, TraineeDTO.class);
    }

    @Test
    void getTraineeByUsername_NotFound() {
        String username = "NonExistent.User";
        when(traineeService.selectTraineeByUsername(username)).thenReturn(Optional.empty());

        Optional<TraineeDTO> result = gymFacade.getTraineeByUsername(username);

        assertFalse(result.isPresent());
        verify(traineeService).selectTraineeByUsername(username);
        verify(modelMapper, never()).map(any(), eq(TraineeDTO.class));
    }

    @Test
    void getTraineeById_Found() {
        Long id = 1L;
        Trainee traineeEntity = new Trainee();
        traineeEntity.setId(id);
        traineeEntity.setFirstName("John");

        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setId(id);
        traineeDTO.setFirstName("John");

        when(traineeService.selectTraineeById(id)).thenReturn(Optional.of(traineeEntity));
        when(modelMapper.map(traineeEntity, TraineeDTO.class)).thenReturn(traineeDTO);

        Optional<TraineeDTO> result = gymFacade.getTraineeById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(traineeService).selectTraineeById(id);
        verify(modelMapper).map(traineeEntity, TraineeDTO.class);
    }

    @Test
    void getTraineeById_NotFound() {
        Long id = 999L;
        when(traineeService.selectTraineeById(id)).thenReturn(Optional.empty());

        Optional<TraineeDTO> result = gymFacade.getTraineeById(id);

        assertFalse(result.isPresent());
        verify(traineeService).selectTraineeById(id);
        verify(modelMapper, never()).map(any(), eq(TraineeDTO.class));
    }

    @Test
    void updateTrainee_Success() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setId(1L);
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");

        Trainee traineeEntity = new Trainee();
        traineeEntity.setId(1L);
        traineeEntity.setFirstName("John");
        traineeEntity.setLastName("Doe");

        when(modelMapper.map(traineeDTO, Trainee.class)).thenReturn(traineeEntity);
        gymFacade.updateTrainee(traineeDTO);

        verify(modelMapper).map(traineeDTO, Trainee.class);
        verify(traineeService).updateTrainee(traineeEntity);
    }

    @Test
    void deleteTrainee_Success() {
        String username = "John.Doe";
        gymFacade.deleteTrainee(username);
        verify(traineeService).deleteTrainee(username);
    }

    @Test
    void createTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setId(1L);
        trainerDTO.setFirstName("Jane");
        trainerDTO.setLastName("Smith");

        Trainer trainerEntity = new Trainer();
        trainerEntity.setId(1L);
        trainerEntity.setFirstName("Jane");
        trainerEntity.setLastName("Smith");

        when(modelMapper.map(trainerDTO, Trainer.class)).thenReturn(trainerEntity);
        gymFacade.createTrainer(trainerDTO);

        verify(modelMapper).map(trainerDTO, Trainer.class);
        verify(trainerService).createTrainer(trainerEntity);
    }

    @Test
    void getTrainerByUsername_Found() {
        String username = "Jane.Smith";
        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setFirstName("Jane");

        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setUsername(username);
        trainerDTO.setFirstName("Jane");

        when(trainerService.selectTrainerByUsername(username)).thenReturn(Optional.of(trainerEntity));
        when(modelMapper.map(trainerEntity, TrainerDTO.class)).thenReturn(trainerDTO);

        Optional<TrainerDTO> result = gymFacade.getTrainerByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(trainerService).selectTrainerByUsername(username);
        verify(modelMapper).map(trainerEntity, TrainerDTO.class);
    }

    @Test
    void getTrainerByUsername_NotFound() {
        String username = "NonExistent.Trainer";
        when(trainerService.selectTrainerByUsername(username)).thenReturn(Optional.empty());

        Optional<TrainerDTO> result = gymFacade.getTrainerByUsername(username);

        assertFalse(result.isPresent());
        verify(trainerService).selectTrainerByUsername(username);
        verify(modelMapper, never()).map(any(), eq(TrainerDTO.class));
    }

    @Test
    void updateTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setId(1L);
        trainerDTO.setFirstName("Jane");
        trainerDTO.setLastName("Smith");

        Trainer trainerEntity = new Trainer();
        trainerEntity.setId(1L);
        trainerEntity.setFirstName("Jane");
        trainerEntity.setLastName("Smith");

        when(modelMapper.map(trainerDTO, Trainer.class)).thenReturn(trainerEntity);
        gymFacade.updateTrainer(trainerDTO);

        verify(modelMapper).map(trainerDTO, Trainer.class);
        verify(trainerService).updateTrainer(trainerEntity);
    }

    @Test
    void createTraining_Success() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setId(1L);
        trainingDTO.setTrainingName("Fitness Training");

        Training trainingEntity = new Training();
        trainingEntity.setId(1L);
        trainingEntity.setTrainingName("Fitness Training");

        when(modelMapper.map(trainingDTO, Training.class)).thenReturn(trainingEntity);
        gymFacade.createTraining(trainingDTO);

        verify(modelMapper).map(trainingDTO, Training.class);
        verify(trainingService).createTraining(trainingEntity);
    }

    @Test
    void getTraining_Found() {
        Long id = 1L;
        Training trainingEntity = new Training();
        trainingEntity.setId(id);
        trainingEntity.setTrainingName("Fitness Training");

        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setId(id);
        trainingDTO.setTrainingName("Fitness Training");

        when(trainingService.selectTraining(id)).thenReturn(Optional.of(trainingEntity));
        when(modelMapper.map(trainingEntity, TrainingDTO.class)).thenReturn(trainingDTO);

        Optional<TrainingDTO> result = gymFacade.getTraining(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(trainingService).selectTraining(id);
        verify(modelMapper).map(trainingEntity, TrainingDTO.class);
    }

    @Test
    void getTraining_NotFound() {
        Long id = 999L;
        when(trainingService.selectTraining(id)).thenReturn(Optional.empty());

        Optional<TrainingDTO> result = gymFacade.getTraining(id);

        assertFalse(result.isPresent());
        verify(trainingService).selectTraining(id);
        verify(modelMapper, never()).map(any(), eq(TrainingDTO.class));
    }

    @Test
    void getAllTrainings_Success() {
        Training training1 = new Training();
        training1.setId(1L);
        training1.setTrainingName("Fitness Training");

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTrainingName("Yoga");

        List<Training> trainingEntities = Arrays.asList(training1, training2);

        TrainingDTO trainingDTO1 = new TrainingDTO();
        trainingDTO1.setId(1L);
        trainingDTO1.setTrainingName("Fitness Training");

        TrainingDTO trainingDTO2 = new TrainingDTO();
        trainingDTO2.setId(2L);
        trainingDTO2.setTrainingName("Yoga");

        when(trainingService.getAllTrainings()).thenReturn(trainingEntities);
        when(modelMapper.map(training1, TrainingDTO.class)).thenReturn(trainingDTO1);
        when(modelMapper.map(training2, TrainingDTO.class)).thenReturn(trainingDTO2);

        List<TrainingDTO> result = gymFacade.getAllTrainings();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Fitness Training", result.get(0).getTrainingName());
        assertEquals("Yoga", result.get(1).getTrainingName());
        verify(trainingService).getAllTrainings();
        verify(modelMapper, times(2)).map(any(Training.class), eq(TrainingDTO.class));
    }

    @Test
    void getAllTrainings_Empty() {
        when(trainingService.getAllTrainings()).thenReturn(Arrays.asList());

        List<TrainingDTO> result = gymFacade.getAllTrainings();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(trainingService).getAllTrainings();
        verify(modelMapper, never()).map(any(Training.class), eq(TrainingDTO.class));
    }
}
