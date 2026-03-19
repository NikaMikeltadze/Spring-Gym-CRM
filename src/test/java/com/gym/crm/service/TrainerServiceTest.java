package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.exception.NotFoundException;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingTypeMapper;
import com.gym.crm.service.impl.TrainerServiceImpl;
import com.gym.crm.util.UsernamePasswordGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("John"), eq("Smith"), any()))
                .thenReturn("John.Smith");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("aB3dEfGh1K");

        RegisterTrainerResponse result = trainerService.createTrainer(trainer);

        verify(trainerDao).save(trainer);
        assertEquals("John.Smith", trainer.getUsername());
        assertEquals("aB3dEfGh1K", trainer.getPassword());
        assertTrue(trainer.getIsActive());
        assertEquals("John.Smith", result.getUsername());
        assertEquals("aB3dEfGh1K", result.getPassword());
    }

    @Test
    void createTrainer_WithDuplicateUsername_GeneratesSerialNumber() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("John"), eq("Smith"), any()))
                .thenReturn("John.Smith1");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("pO2iYlC7wN");

        RegisterTrainerResponse result = trainerService.createTrainer(trainer);

        verify(trainerDao).save(trainer);
        assertEquals("John.Smith1", trainer.getUsername());
        assertEquals("pO2iYlC7wN", trainer.getPassword());
        assertTrue(trainer.getIsActive());
        assertEquals("John.Smith1", result.getUsername());
    }

    @Test
    void createTrainer_WhenUsernameExistsAsTrainee_Throws() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sarah");
        trainer.setLastName("Williams");

        when(usernamePasswordGenerator.generateUsername(eq("Sarah"), eq("Williams"), any()))
                .thenReturn("Sarah.Williams");
        when(traineeDao.exists("Sarah.Williams")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> trainerService.createTrainer(trainer));

        verify(trainerDao, never()).save(any(Trainer.class));
    }

    @Test
    void updateTrainer_Success() {
        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setUsername("John.Smith");
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setIsActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("John.Smith");
        trainer.setFirstName("John");
        trainer.setLastName("Smith");
        trainer.setIsActive(true);

        UpdateTrainerProfileResponse expected = UpdateTrainerProfileResponse.builder()
                .username("John.Smith")
                .firstName("John")
                .lastName("Smith")
                .trainingTypeId(null)
                .isActive(true)
                .traineeList(List.of())
                .build();

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toUpdateProfileResponse(trainer)).thenReturn(expected);

        UpdateTrainerProfileResponse result = trainerService.updateTrainer(request);

        assertEquals(expected, result);
        verify(trainerMapper).updateEntityFromRequest(request, trainer);
        verify(trainerDao).update(trainer);
    }

    @Test
    void selectTrainerById_Success() {
        Long id = 1L;
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        when(trainerDao.findById(id)).thenReturn(Optional.of(expectedTrainer));

        Optional<Trainer> result = trainerService.selectTrainerById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(trainerDao).findById(id);
    }

    @Test
    void selectTrainerById_NotFound() {
        Long id = 999L;
        when(trainerDao.findById(id)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.selectTrainerById(id);

        assertFalse(result.isPresent());
        verify(trainerDao).findById(id);
    }

    @Test
    void selectTrainerByUsername_Success() {
        String username = "John.Smith";
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setUsername(username);
        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(expectedTrainer));

        Optional<Trainer> result = trainerService.selectTrainerByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(trainerDao).findByUsername(username);
    }

    @Test
    void selectTrainerByUsername_NotFound() {
        String username = "NonExistent.Trainer";
        when(trainerDao.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.selectTrainerByUsername(username);

        assertFalse(result.isPresent());
        verify(trainerDao).findByUsername(username);
    }

    @Test
    void changePassword_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setPassword("aB3dEfGh1K");

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainerService.changePassword("John.Smith", "aB3dEfGh1K", "newPass1234");

        assertEquals("newPass1234", trainer.getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    void changePassword_WrongOldPassword() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setPassword("aB3dEfGh1K");

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.changePassword("John.Smith", "wrongPass12", "newPass1234"));
    }

    @Test
    void changePassword_SameAsOldPassword() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setPassword("aB3dEfGh1K");

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.changePassword("John.Smith", "aB3dEfGh1K", "aB3dEfGh1K"));
    }

    @Test
    void changePassword_TrainerNotFound() {
        when(trainerDao.findByUsername("NonExistent.Trainer")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainerService.changePassword("NonExistent.Trainer", "oldPass", "newPass"));
    }

    @Test
    void activateTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setIsActive(false);

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainerService.activateTrainer("John.Smith");

        assertTrue(trainer.getIsActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void activateTrainer_AlreadyActive() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setIsActive(true);

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.activateTrainer("John.Smith"));
    }

    @Test
    void activateTrainer_NotFound() {
        when(trainerDao.findByUsername("NonExistent.Trainer")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.activateTrainer("NonExistent.Trainer"));
    }

    @Test
    void deactivateTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setIsActive(true);

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainerService.deactivateTrainer("John.Smith");

        assertFalse(trainer.getIsActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void deactivateTrainer_AlreadyInactive() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setIsActive(false);

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.deactivateTrainer("John.Smith"));
    }

    @Test
    void deactivateTrainer_NotFound() {
        when(trainerDao.findByUsername("NonExistent.Trainer")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.deactivateTrainer("NonExistent.Trainer"));
    }

    @Test
    void getTrainings_Success() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("Fitness");

        Training training = Training.builder()
                .trainingName("Morning Fitness Bootcamp")
                .trainingType(trainingType)
                .build();

        TrainingTypeInfo info = new TrainingTypeInfo(1L, "Fitness");

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(new Trainer()));
        when(trainingDao.findByTrainerUsernameAndCriteria(eq("John.Smith"), any(), any(), any()))
                .thenReturn(List.of(training));
        when(trainingTypeMapper.toTrainingTypeInfo(trainingType)).thenReturn(info);

        GetTrainingTypesResponse result = trainerService.getTrainings("John.Smith", null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTrainingTypeList().size());
        assertEquals("Fitness", result.getTrainingTypeList().get(0).getTrainingTypeName());
    }
}
