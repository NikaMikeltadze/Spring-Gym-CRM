package com.gym.crm.service;

import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.model.Trainee;
import com.gym.crm.util.UsernamePasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDaoImpl traineeDaoImpl;

    @Mock
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService.setUsernamePasswordGenerator(usernamePasswordGenerator);
    }

    @Test
    void createTrainee_Success() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(usernamePasswordGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                .thenReturn("John.Doe");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("abcd123456");

        // When
        traineeService.createTrainee(trainee);

        // Then
        verify(traineeDaoImpl).save(trainee);
        assertEquals("John.Doe", trainee.getUsername());
        assertEquals("abcd123456", trainee.getPassword());
        assertTrue(trainee.isActive());
    }

    @Test
    void createTrainee_NullTrainee_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.createTrainee(null)
        );
        assertEquals("Trainee must not be null", exception.getMessage());
    }

    @Test
    void createTrainee_BlankFirstName_ThrowsException() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setFirstName("");
        trainee.setLastName("Doe");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.createTrainee(trainee)
        );
        assertEquals("First name must not be blank", exception.getMessage());
    }

    @Test
    void createTrainee_BlankLastName_ThrowsException() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.createTrainee(trainee)
        );
        assertEquals("Last name must not be blank", exception.getMessage());
    }

    @Test
    void updateTrainee_Success() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("John.Doe");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        // When
        traineeService.updateTrainee(trainee);

        // Then
        verify(traineeDaoImpl).update(trainee);
    }

    @Test
    void updateTrainee_NullTrainee_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.updateTrainee(null)
        );
        assertEquals("Trainee and trainee.id must not be null", exception.getMessage());
    }

    @Test
    void updateTrainee_NullId_ThrowsException() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Doe");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.updateTrainee(trainee)
        );
        assertEquals("Trainee and trainee.id must not be null", exception.getMessage());
    }

    @Test
    void updateTrainee_BlankUsername_ThrowsException() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.updateTrainee(trainee)
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }

    @Test
    void deleteTrainee_Success() {
        // Given
        String username = "John.Doe";

        // When
        traineeService.deleteTrainee(username);

        // Then
        verify(traineeDaoImpl).delete(username);
    }

    @Test
    void deleteTrainee_BlankUsername_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.deleteTrainee("")
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }

    @Test
    void selectTraineeByUsername_Success() {
        // Given
        String username = "John.Doe";
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setUsername(username);
        when(traineeDaoImpl.findByUsername(username)).thenReturn(expectedTrainee);

        // When
        Trainee result = traineeService.selectTraineeByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(traineeDaoImpl).findByUsername(username);
    }

    @Test
    void selectTraineeByUsername_BlankUsername_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.selectTraineeByUsername("")
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }

    @Test
    void selectTraineeById_Success() {
        // Given
        Long id = 1L;
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(id);
        when(traineeDaoImpl.findById(id)).thenReturn(expectedTrainee);

        // When
        Trainee result = traineeService.selectTraineeById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(traineeDaoImpl).findById(id);
    }

    @Test
    void selectTraineeById_NullId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                traineeService.selectTraineeById(null)
        );
        assertEquals("Id must not be null", exception.getMessage());
    }
}
