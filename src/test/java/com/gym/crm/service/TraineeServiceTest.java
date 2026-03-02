package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.service.impl.TraineeServiceImpl;
import com.gym.crm.util.UsernamePasswordGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(usernamePasswordGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                .thenReturn("John.Doe");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("abcd123456");

        traineeService.createTrainee(trainee);

        verify(traineeDao).save(trainee);
        assertEquals("John.Doe", trainee.getUsername());
        assertEquals("abcd123456", trainee.getPassword());
        assertTrue(trainee.getIsActive());
    }

    @Test
    void updateTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("John.Doe");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        traineeService.updateTrainee(trainee);

        verify(traineeDao).update(trainee);
    }

    @Test
    void deleteTrainee_Success() {
        String username = "John.Doe";

        traineeService.deleteTrainee(username);

        verify(traineeDao).delete(username);
    }

    @Test
    void selectTraineeByUsername_Success() {
        String username = "John.Doe";
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setUsername(username);
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(expectedTrainee));

        Optional<Trainee> result = traineeService.selectTraineeByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(traineeDao).findByUsername(username);
    }

    @Test
    void selectTraineeById_Success() {
        Long id = 1L;
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(id);
        when(traineeDao.findById(id)).thenReturn(Optional.of(expectedTrainee));

        Optional<Trainee> result = traineeService.selectTraineeById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(traineeDao).findById(id);
    }

    @Test
    void selectTraineeById_NotFound() {
        Long id = 999L;
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.selectTraineeById(id);

        assertFalse(result.isPresent());
        verify(traineeDao).findById(id);
    }

    @Test
    void selectTraineeByUsername_NotFound() {
        String username = "NonExistent.User";
        when(traineeDao.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.selectTraineeByUsername(username);

        assertFalse(result.isPresent());
        verify(traineeDao).findByUsername(username);
    }

    @Test
    void createTrainee_WithDuplicateUsername_GeneratesSerialNumber() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(usernamePasswordGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                .thenReturn("John.Doe2");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("abcd123456");

        traineeService.createTrainee(trainee);

        verify(traineeDao).save(trainee);
        assertEquals("John.Doe2", trainee.getUsername());
        assertEquals("abcd123456", trainee.getPassword());
        assertTrue(trainee.getIsActive());
    }

    @Test
    void deleteTrainee_VerifiesTraineeIsDeleted() {
        String username = "John.Doe";

        traineeService.deleteTrainee(username);

        verify(traineeDao).delete(username);
    }
}

