package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.client.TrainerWorkloadClient;
import com.gym.crm.client.WorkloadRequest;
import com.gym.crm.client.WorkloadSummaryResponse;
import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.trainer.GetTrainerMonthlyWorkloadResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.mapper.TrainingTypeMapper;
import com.gym.crm.exception.NotFoundException;
import com.gym.crm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private TrainerWorkloadClient workloadClient;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_Success() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Sarah.Williams");
        request.setTrainerUsername("John.Smith");
        request.setTrainingName("Morning Fitness Bootcamp");
        request.setTrainingDate(LocalDate.of(2026, 3, 1));
        request.setTrainingDuration(60.5);

        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        trainee.getUser().setUsername("Sarah.Williams");

        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setUsername("John.Smith");
        trainer.getUser().setFirstName("John");
        trainer.getUser().setLastName("Smith");
        trainer.getUser().setIsActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("Fitness");

        TrainingTypeInfo expectedInfo = new TrainingTypeInfo(1L, "Fitness");

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("Morning Fitness Bootcamp")).thenReturn(Optional.of(trainingType));
        when(trainingTypeMapper.toTrainingTypeInfo(trainingType)).thenReturn(expectedInfo);

        TrainingTypeInfo result = trainingService.createTraining(request);

        verify(trainingDao).save(any(Training.class));
        ArgumentCaptor<WorkloadRequest> workloadCaptor = ArgumentCaptor.forClass(WorkloadRequest.class);
        verify(workloadClient).updateWorkload(workloadCaptor.capture());
        assertEquals(60.5, workloadCaptor.getValue().getTrainingDuration());
        assertEquals(WorkloadRequest.ActionType.ADD, workloadCaptor.getValue().getActionType());
        assertNotNull(result);
        assertEquals(1L, result.getTrainingTypeId());
        assertEquals("Fitness", result.getTrainingTypeName());
    }

    @Test
    void createTraining_TraineeNotFound() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("NonExistent.Trainee");
        request.setTrainerUsername("John.Smith");
        request.setTrainingName("Morning Fitness Bootcamp");

        when(traineeDao.findByUsername("NonExistent.Trainee")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void createTraining_TrainerNotFound() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Sarah.Williams");
        request.setTrainerUsername("NonExistent.Trainer");
        request.setTrainingName("Morning Fitness Bootcamp");

        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("NonExistent.Trainer")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void deleteTraining_Success() {
        Long trainingId = 7L;

        User trainerUser = new User();
        trainerUser.setUsername("John.Smith");
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Smith");
        trainerUser.setIsActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        Training training = new Training();
        training.setId(trainingId);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2026, 3, 1));
        training.setTrainingDuration(45.0);

        when(trainingDao.findById(trainingId)).thenReturn(Optional.of(training));

        trainingService.deleteTraining(trainingId);

        verify(trainingDao).deleteById(trainingId);
        ArgumentCaptor<WorkloadRequest> workloadCaptor = ArgumentCaptor.forClass(WorkloadRequest.class);
        verify(workloadClient).updateWorkload(workloadCaptor.capture());
        assertEquals(WorkloadRequest.ActionType.DELETE, workloadCaptor.getValue().getActionType());
        assertEquals(45.0, workloadCaptor.getValue().getTrainingDuration());
    }

    @Test
    void deleteTraining_NotFound() {
        Long trainingId = 404L;
        when(trainingDao.findById(trainingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.deleteTraining(trainingId));

        verify(trainingDao, never()).deleteById(any());
        verify(workloadClient, never()).updateWorkload(any());
    }

    @Test
    void getTrainerMonthlyWorkload_Success() {
        String username = "John.Smith";
        int year = 2026;
        int month = 4;

        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setUsername(username);

        WorkloadSummaryResponse workloadSummary = WorkloadSummaryResponse.builder()
                .trainerUsername(username)
                .year(year)
                .month(month)
                .trainingSummaryDuration(12.5)
                .build();

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(workloadClient.getMonthlyWorkload(username, year, month)).thenReturn(workloadSummary);

        GetTrainerMonthlyWorkloadResponse result = trainingService.getTrainerMonthlyWorkload(username, year, month);

        assertNotNull(result);
        assertEquals(username, result.getTrainerUsername());
        assertEquals(year, result.getYear());
        assertEquals(month, result.getMonth());
        assertEquals(12.5, result.getTrainingSummaryDuration());
        verify(workloadClient).getMonthlyWorkload(username, year, month);
    }

    @Test
    void selectTraining_Success() {
        Long id = 1L;
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("Fitness");

        Training training = Training.builder()
                .id(id)
                .trainingName("Morning Fitness Bootcamp")
                .trainingType(trainingType)
                .build();

        TrainingTypeInfo expectedInfo = new TrainingTypeInfo(1L, "Fitness");

        when(trainingDao.findById(id)).thenReturn(Optional.of(training));
        when(trainingTypeMapper.toTrainingTypeInfo(trainingType)).thenReturn(expectedInfo);

        Optional<TrainingTypeInfo> result = trainingService.selectTraining(id);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getTrainingTypeId());
        assertEquals("Fitness", result.get().getTrainingTypeName());
        verify(trainingDao).findById(id);
    }

    @Test
    void selectTraining_NotFound() {
        Long id = 999L;
        when(trainingDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainingService.selectTraining(id));
        verify(trainingDao).findById(id);
    }

    @Test
    void getAllTrainings_Success() {
        TrainingType type1 = new TrainingType(1L, "Fitness");
        TrainingType type2 = new TrainingType(2L, "Yoga");

        Training training1 = Training.builder().id(1L).trainingName("Morning Fitness Bootcamp").trainingType(type1).build();
        Training training2 = Training.builder().id(2L).trainingName("Evening Yoga Session").trainingType(type2).build();

        when(trainingDao.findAll()).thenReturn(List.of(training1, training2));
        when(trainingTypeMapper.toTrainingTypeInfo(type1)).thenReturn(new TrainingTypeInfo(1L, "Fitness"));
        when(trainingTypeMapper.toTrainingTypeInfo(type2)).thenReturn(new TrainingTypeInfo(2L, "Yoga"));

        GetTrainingTypesResponse result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(2, result.getTrainingTypeList().size());
        assertEquals("Fitness", result.getTrainingTypeList().get(0).getTrainingTypeName());
        assertEquals("Yoga", result.getTrainingTypeList().get(1).getTrainingTypeName());
        verify(trainingDao).findAll();
    }

    @Test
    void getAllTrainings_Empty() {
        when(trainingDao.findAll()).thenReturn(List.of());

        GetTrainingTypesResponse result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(0, result.getTrainingTypeList().size());
        verify(trainingDao).findAll();
    }

    @Test
    void findTrainingsByDateRange_Success() {
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 31);

        TrainingType type = new TrainingType(1L, "Fitness");
        Training training = Training.builder().id(1L).trainingName("Morning Fitness Bootcamp").trainingType(type).build();

        when(trainingDao.findByDateRange(from, to)).thenReturn(List.of(training));
        when(trainingTypeMapper.toTrainingTypeInfo(type)).thenReturn(new TrainingTypeInfo(1L, "Fitness"));

        GetTrainingTypesResponse result = trainingService.findTrainingsByDateRange(from, to);

        assertNotNull(result);
        assertEquals(1, result.getTrainingTypeList().size());
        verify(trainingDao).findByDateRange(from, to);
    }
}
