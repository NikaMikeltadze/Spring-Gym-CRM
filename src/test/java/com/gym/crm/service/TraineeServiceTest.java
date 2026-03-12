package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.GetTraineeTrainingsResponse;
import com.gym.crm.dto.response.trainee.RegisterTraineeResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeProfileResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.impl.TraineeServiceImpl;
import com.gym.crm.util.UsernamePasswordGenerator;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Sarah");
        trainee.setLastName("Williams");

        when(usernamePasswordGenerator.generateUsername(eq("Sarah"), eq("Williams"), any()))
                .thenReturn("Sarah.Williams");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("qF5nBrM3gZ");

        RegisterTraineeResponse result = traineeService.createTrainee(trainee);

        verify(traineeDao).save(trainee);
        assertEquals("Sarah.Williams", trainee.getUsername());
        assertEquals("qF5nBrM3gZ", trainee.getPassword());
        assertTrue(trainee.getIsActive());
        assertEquals("Sarah.Williams", result.getUsername());
        assertEquals("qF5nBrM3gZ", result.getPassword());
    }

    @Test
    void createTrainee_WithDuplicateUsername_GeneratesSerialNumber() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("John"), eq("Smith"), any()))
                .thenReturn("John.Smith1");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("pO2iYlC7wN");

        RegisterTraineeResponse result = traineeService.createTrainee(trainee);

        verify(traineeDao).save(trainee);
        assertEquals("John.Smith1", trainee.getUsername());
        assertEquals("pO2iYlC7wN", trainee.getPassword());
        assertTrue(trainee.getIsActive());
        assertEquals("John.Smith1", result.getUsername());
    }

    @Test
    void updateTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);
        trainee.setUsername("Sarah.Williams");
        trainee.setFirstName("Sarah");
        trainee.setLastName("Williams");
        trainee.setDateOfBirth(LocalDate.of(1995, 6, 15));
        trainee.setAddress("123 Main St New York NY");
        trainee.setIsActive(true);
        trainee.setTrainers(new ArrayList<>());

        UpdateTraineeProfileResponse result = traineeService.updateTrainee(trainee);

        verify(traineeDao).update(trainee);
        assertNotNull(result);
        assertEquals("Sarah.Williams", result.getUsername());
        assertEquals("Sarah", result.getFirstName());
        assertEquals("Williams", result.getLastName());
    }

    @Test
    void deleteTrainee_Success() {
        traineeService.deleteTrainee("Sarah.Williams");
        verify(traineeDao).delete("Sarah.Williams");
    }

    @Test
    void selectTraineeByUsername_Success() {
        String username = "Sarah.Williams";
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setFirstName("Sarah");
        trainee.setLastName("Williams");
        trainee.setTrainers(new ArrayList<>());

        GetTraineeProfileResponse profileResponse = GetTraineeProfileResponse.builder()
                .firstName("Sarah")
                .lastName("Williams")
                .isActive(true)
                .build();

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toGetProfileResponse(trainee)).thenReturn(profileResponse);

        Optional<GetTraineeProfileResponse> result = traineeService.selectTraineeByUsername(username);

        assertTrue(result.isPresent());
        assertEquals("Sarah", result.get().getFirstName());
        verify(traineeDao).findByUsername(username);
    }

    @Test
    void selectTraineeByUsername_NotFound() {
        String username = "NonExistent.User";
        when(traineeDao.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> traineeService.selectTraineeByUsername(username));
        verify(traineeDao).findByUsername(username);
    }

    @Test
    void selectTraineeById_Success() {
        Long id = 5L;
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setFirstName("Sarah");
        trainee.setTrainers(new ArrayList<>());

        GetTraineeProfileResponse profileResponse = GetTraineeProfileResponse.builder()
                .firstName("Sarah")
                .isActive(true)
                .build();

        when(traineeDao.findById(id)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toGetProfileResponse(trainee)).thenReturn(profileResponse);

        Optional<GetTraineeProfileResponse> result = traineeService.selectTraineeById(id);

        assertTrue(result.isPresent());
        assertEquals("Sarah", result.get().getFirstName());
        verify(traineeDao).findById(id);
    }

    @Test
    void selectTraineeById_NotFound() {
        Long id = 999L;
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> traineeService.selectTraineeById(id));
        verify(traineeDao).findById(id);
    }

    @Test
    void changePassword_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setPassword("qF5nBrM3gZ");

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Sarah.Williams");
        request.setPassword("qF5nBrM3gZ");
        request.setNewPassword("newPass1234");

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        traineeService.changePassword(request);

        assertEquals("newPass1234", trainee.getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void changePassword_WrongOldPassword() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setPassword("qF5nBrM3gZ");

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Sarah.Williams");
        request.setPassword("wrongPass12");
        request.setNewPassword("newPass1234");

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalArgumentException.class, () -> traineeService.changePassword(request));
    }

    @Test
    void changePassword_SameAsOldPassword() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setPassword("qF5nBrM3gZ");

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Sarah.Williams");
        request.setPassword("qF5nBrM3gZ");
        request.setNewPassword("qF5nBrM3gZ");

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalArgumentException.class, () -> traineeService.changePassword(request));
    }

    @Test
    void activateTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setIsActive(false);

        ActivateTraineeRequest request = new ActivateTraineeRequest();
        request.setUsername("Sarah.Williams");
        request.setIsActive(true);

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        traineeService.activateTrainee(request);

        assertTrue(trainee.getIsActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void activateTrainee_AlreadyActive() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setIsActive(true);

        ActivateTraineeRequest request = new ActivateTraineeRequest();
        request.setUsername("Sarah.Williams");
        request.setIsActive(true);

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> traineeService.activateTrainee(request));
    }

    @Test
    void deactivateTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setIsActive(true);

        DeactivateTraineeRequest request = new DeactivateTraineeRequest();
        request.setUsername("Sarah.Williams");
        request.setIsActive(false);

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        traineeService.deactivateTrainee(request);

        assertFalse(trainee.getIsActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void deactivateTrainee_AlreadyInactive() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setIsActive(false);

        DeactivateTraineeRequest request = new DeactivateTraineeRequest();
        request.setUsername("Sarah.Williams");
        request.setIsActive(false);

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> traineeService.deactivateTrainee(request));
    }

    @Test
    void getTrainings_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");

        GetTraineeTrainingsRequest request = new GetTraineeTrainingsRequest();
        request.setUsername("Sarah.Williams");

        Training training = Training.builder()
                .trainingName("Morning Fitness Bootcamp")
                .trainingDate(LocalDate.of(2026, 3, 1))
                .trainingDuration(60.0)
                .build();

        GetTraineeTrainingsResponse response = GetTraineeTrainingsResponse.builder()
                .trainingName("Morning Fitness Bootcamp")
                .trainingDate(LocalDate.of(2026, 3, 1))
                .trainingDuration(60.0)
                .build();

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));
        when(trainingDao.findByTraineeUsernameAndCriteria(eq("Sarah.Williams"), any(), any(), any(), any()))
                .thenReturn(List.of(training));
        when(trainingMapper.toGetTraineeTrainingsResponse(training)).thenReturn(response);

        List<GetTraineeTrainingsResponse> result = traineeService.getTrainings(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Morning Fitness Bootcamp", result.get(0).getTrainingName());
    }

    @Test
    void updateTrainerList_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Sarah.Williams");
        trainee.setTrainers(new ArrayList<>());

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("Fitness");

        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setFirstName("John");
        trainer.setLastName("Smith");
        trainer.setTrainingType(trainingType);

        UpdateTraineeTrainerListRequest request = new UpdateTraineeTrainerListRequest();
        request.setTraineeUsername("Sarah.Williams");
        request.setTrainerUsernameList(List.of("John.Smith"));

        TrainerProfileInfo info = new TrainerProfileInfo("John.Smith", "John", "Smith", 1L);

        when(traineeDao.findByUsername("Sarah.Williams")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileInfo(trainer)).thenReturn(info);

        List<TrainerProfileInfo> result = traineeService.updateTrainerList(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John.Smith", result.get(0).getUsername());
        verify(traineeDao).update(trainee);
    }

    @Test
    void getUnassignedActiveTrainers_Success() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(5L);
        trainingType.setName("Resistance");

        Trainer trainer = new Trainer();
        trainer.setUsername("Robert.Brown");
        trainer.setFirstName("Robert");
        trainer.setLastName("Brown");
        trainer.setTrainingType(trainingType);

        TraineeAssignableTrainerRequest request = new TraineeAssignableTrainerRequest();
        request.setUsername("Sarah.Williams");

        TrainerProfileInfo info = new TrainerProfileInfo("Robert.Brown", "Robert", "Brown", 5L);

        when(trainerDao.findActiveTrainersNotAssignedTo("Sarah.Williams")).thenReturn(List.of(trainer));
        when(trainerMapper.toProfileInfo(trainer)).thenReturn(info);

        List<TrainerProfileInfo> result = traineeService.getUnassignedActiveTrainers(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Robert.Brown", result.get(0).getUsername());
    }
}
