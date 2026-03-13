package com.gym.crm.facade.impl;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.ActivateTraineeRequest;
import com.gym.crm.dto.request.trainee.DeactivateTraineeRequest;
import com.gym.crm.dto.request.trainee.GetTraineeTrainingsRequest;
import com.gym.crm.dto.request.trainee.UpdateTraineeProfileRequest;
import com.gym.crm.dto.request.trainee.UpdateTraineeTrainerListRequest;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.GetTraineeTrainingsResponse;
import com.gym.crm.dto.response.trainee.RegisterTraineeResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeTrainerListResponse;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.dto.request.trainee.RegisterTraineeRequest;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private GymFacadeImpl gymFacade;

    @Test
    void createTrainee_Success() {
        RegisterTraineeRequest request = new RegisterTraineeRequest();
        request.setFirstName("Sarah");
        request.setLastName("Williams");

        Trainee trainee = new Trainee();
        trainee.setFirstName("Sarah");
        trainee.setLastName("Williams");

        RegisterTraineeResponse expected = new RegisterTraineeResponse("Sarah.Williams", "qF5nBrM3gZ");
        when(traineeMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(expected);

        RegisterTraineeResponse result = gymFacade.createTrainee(request);

        assertEquals(expected, result);
        verify(traineeMapper).toEntity(request);
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    void getTraineeByUsername_Found() {
        String username = "Sarah.Williams";
        GetTraineeProfileResponse response = GetTraineeProfileResponse.builder()
                .firstName("Sarah")
                .lastName("Williams")
                .dateOfBirth(LocalDate.of(1995, 6, 15))
                .address("123 Main St New York NY")
                .isActive(true)
                .build();

        when(traineeService.selectTraineeByUsername(username)).thenReturn(Optional.of(response));

        GetTraineeProfileResponse result = gymFacade.getTraineeByUsername(username);

        assertEquals("Sarah", result.getFirstName());
        verify(traineeService).selectTraineeByUsername(username);
    }

    @Test
    void getTraineeByUsername_NotFound() {
        String username = "NonExistent.User";
        when(traineeService.selectTraineeByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> gymFacade.getTraineeByUsername(username));
        verify(traineeService).selectTraineeByUsername(username);
    }

    @Test
    void getTraineeById_Found() {
        Long id = 5L;
        GetTraineeProfileResponse response = GetTraineeProfileResponse.builder()
                .firstName("Sarah")
                .lastName("Williams")
                .dateOfBirth(LocalDate.of(1995, 6, 15))
                .address("123 Main St New York NY")
                .isActive(true)
                .build();

        when(traineeService.selectTraineeById(id)).thenReturn(Optional.of(response));

        Optional<GetTraineeProfileResponse> result = gymFacade.getTraineeById(id);

        assertTrue(result.isPresent());
        assertEquals("Sarah", result.get().getFirstName());
        verify(traineeService).selectTraineeById(id);
    }

    @Test
    void getTraineeById_NotFound() {
        Long id = 999L;
        when(traineeService.selectTraineeById(id)).thenReturn(Optional.empty());

        Optional<GetTraineeProfileResponse> result = gymFacade.getTraineeById(id);

        assertFalse(result.isPresent());
        verify(traineeService).selectTraineeById(id);
    }

    @Test
    void updateTrainee_Success() {
        UpdateTraineeProfileRequest request = new UpdateTraineeProfileRequest();
        request.setUsername("Sarah.Williams");
        request.setFirstName("Sarah");
        request.setLastName("Williams");
        request.setDateOfBirth(LocalDate.of(1995, 6, 15));
        request.setAddress("123 Main St New York NY");
        request.setIsActive(true);

        UpdateTraineeProfileResponse expected = UpdateTraineeProfileResponse.builder()
                .username("Sarah.Williams")
                .firstName("Sarah")
                .lastName("Williams")
                .dateOfBirth(LocalDate.of(1995, 6, 15))
                .address("123 Main St New York NY")
                .isActive(true)
                .build();

        when(traineeService.updateTrainee(request)).thenReturn(expected);

        UpdateTraineeProfileResponse result = gymFacade.updateTrainee(request);

        assertEquals(expected, result);
        verify(traineeService).updateTrainee(request);
    }

    @Test
    void deleteTrainee_Success() {
        gymFacade.deleteTrainee("Sarah.Williams");
        verify(traineeService).deleteTrainee("Sarah.Williams");
    }

    @Test
    void createTrainer_Success() {
        RegisterTrainerRequest request = new RegisterTrainerRequest();
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setTrainingTypeId(1L);

        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Smith");

        RegisterTrainerResponse expected = new RegisterTrainerResponse("John.Smith", "aB3dEfGh1K");
        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(expected);

        RegisterTrainerResponse result = gymFacade.createTrainer(request);

        assertEquals(expected, result);
        verify(trainerMapper).toEntity(request);
        verify(trainerService).createTrainer(trainer);
    }

    @Test
    void getTrainerByUsername_Found() {
        String username = "John.Smith";

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("Fitness");

        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setFirstName("John");
        trainerEntity.setLastName("Smith");
        trainerEntity.setIsActive(true);
        trainerEntity.setTrainingType(trainingType);
        trainerEntity.setTrainees(new ArrayList<>());

        when(trainerService.selectTrainerByUsername(username)).thenReturn(Optional.of(trainerEntity));

        Optional<GetTrainerProfileResponse> result = gymFacade.getTrainerByUsername(username);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        verify(trainerService).selectTrainerByUsername(username);
    }

    @Test
    void getTrainerByUsername_NotFound() {
        String username = "NonExistent.Trainer";
        when(trainerService.selectTrainerByUsername(username)).thenReturn(Optional.empty());

        Optional<GetTrainerProfileResponse> result = gymFacade.getTrainerByUsername(username);

        assertFalse(result.isPresent());
        verify(trainerService).selectTrainerByUsername(username);
    }

    @Test
    void updateTrainer_Success() {
        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setUsername("John.Smith");
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setIsActive(true);

        UpdateTrainerProfileResponse expected = UpdateTrainerProfileResponse.builder()
                .username("John.Smith")
                .firstName("John")
                .lastName("Smith")
                .trainingTypeId(1L)
                .isActive(true)
                .traineeList(List.of())
                .build();

        when(trainerService.updateTrainer(request)).thenReturn(expected);

        UpdateTrainerProfileResponse result = gymFacade.updateTrainer(request);

        assertEquals(expected, result);
        verify(trainerService).updateTrainer(request);
    }

    @Test
    void createTraining_Success() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Sarah.Williams");
        request.setTrainerUsername("John.Smith");
        request.setTrainingName("Morning Fitness Bootcamp");
        request.setTrainingDate(LocalDate.of(2026, 3, 1));
        request.setTrainingDuration(60.0);

        gymFacade.createTraining(request);

        verify(trainingService).createTraining(request);
    }

    @Test
    void getAllTrainings_Success() {
        TrainingTypeInfo info1 = new TrainingTypeInfo(1L, "Fitness");
        TrainingTypeInfo info2 = new TrainingTypeInfo(2L, "Yoga");
        GetTrainingTypesResponse expected = new GetTrainingTypesResponse(List.of(info1, info2));

        when(trainingService.getAllTrainings()).thenReturn(expected);

        GetTrainingTypesResponse result = gymFacade.getAllTrainings();

        assertNotNull(result);
        assertEquals(2, result.getTrainingTypeList().size());
        verify(trainingService).getAllTrainings();
    }

    @Test
    void changeUserPassword_Success() {
        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Sarah.Williams");
        request.setPassword("qF5nBrM3gZ");
        request.setNewPassword("newPass1234");

        gymFacade.changeUserPassword(request);

        verify(userService).changePassword(request);
    }

    @Test
    void activateTrainee_Success() {
        ActivateTraineeRequest request = new ActivateTraineeRequest();
        request.setUsername("Sarah.Williams");
        request.setIsActive(true);

        gymFacade.activateTrainee(request);

        verify(traineeService).activateTrainee(request);
    }

    @Test
    void deactivateTrainee_Success() {
        DeactivateTraineeRequest request = new DeactivateTraineeRequest("Sarah.Williams", false);

        gymFacade.deactivateTrainee(request);

        verify(traineeService).deactivateTrainee(request);
    }

    @Test
    void activateTrainer_Success() {
        gymFacade.activateTrainer("John.Smith");
        verify(trainerService).activateTrainer("John.Smith");
    }

    @Test
    void deactivateTrainer_Success() {
        gymFacade.deactivateTrainer("John.Smith");
        verify(trainerService).deactivateTrainer("John.Smith");
    }

    @Test
    void getTraineeTrainings_Success() {
        GetTraineeTrainingsRequest request = new GetTraineeTrainingsRequest();
        request.setUsername("Sarah.Williams");

        GetTraineeTrainingsResponse response = GetTraineeTrainingsResponse.builder()
                .trainingName("Morning Fitness Bootcamp")
                .trainingDate(LocalDate.of(2026, 3, 1))
                .trainingDuration(60.0)
                .build();

        when(traineeService.getTrainings(request)).thenReturn(List.of(response));

        List<GetTraineeTrainingsResponse> result = gymFacade.getTraineeTrainings(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Morning Fitness Bootcamp", result.get(0).getTrainingName());
        verify(traineeService).getTrainings(request);
    }

    @Test
    void updateTrainerList_Success() {
        UpdateTraineeTrainerListRequest request = new UpdateTraineeTrainerListRequest();
        request.setTraineeUsername("Sarah.Williams");
        request.setTrainerUsernameList(List.of("John.Smith"));

        TrainerProfileInfo info = new TrainerProfileInfo("John.Smith", "John", "Smith", 1L);
        UpdateTraineeTrainerListResponse response = UpdateTraineeTrainerListResponse.builder()
                .trainerList(List.of(info))
                .build();
        when(traineeService.updateTrainerList(request)).thenReturn(response);

        UpdateTraineeTrainerListResponse result = gymFacade.updateTrainerList(request);

        assertNotNull(result);
        assertEquals(1, result.getTrainerList().size());
        assertEquals("John.Smith", result.getTrainerList().get(0).getUsername());
        verify(traineeService).updateTrainerList(request);
    }

    @Test
    void getUnassignedActiveTrainers_Success() {
        TrainerProfileInfo info = new TrainerProfileInfo("Robert.Brown", "Robert", "Brown", 5L);
        when(traineeService.getUnassignedActiveTrainers(any())).thenReturn(List.of(info));

        List<TrainerProfileInfo> result = gymFacade.getUnassignedActiveTrainers("Sarah.Williams");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Robert.Brown", result.get(0).getUsername());
        verify(traineeService).getUnassignedActiveTrainers(any());
    }

    @Test
    void getTrainerTrainings_Success() {
        GetTrainerTrainingsRequest request = new GetTrainerTrainingsRequest();
        request.setUsername("John.Smith");

        GetTrainerTrainingsResponse response = GetTrainerTrainingsResponse.builder()
                .trainingName("Morning Fitness Bootcamp")
                .trainingDate(LocalDate.of(2026, 3, 1))
                .trainingDuration(60.0)
                .trainingTypeName("Fitness")
                .traineeName("Sarah.Williams")
                .build();
        when(traineeService.getTrainerTrainings(request)).thenReturn(List.of(response));

        List<GetTrainerTrainingsResponse> result = gymFacade.getTrainerTrainings(request);

        assertEquals(1, result.size());
        assertEquals("Morning Fitness Bootcamp", result.get(0).getTrainingName());
        verify(traineeService).getTrainerTrainings(request);
    }
}
