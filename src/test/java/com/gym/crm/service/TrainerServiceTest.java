package com.gym.crm.service;

import com.gym.crm.dao.impl.TrainerDaoImpl;
import com.gym.crm.model.Trainer;
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
class TrainerServiceTest {

    @Mock
    private TrainerDaoImpl trainerDaoImpl;

    @Mock
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService.setUsernamePasswordGenerator(usernamePasswordGenerator);
    }

    @Test
    void createTrainer_Success() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("Jane"), eq("Smith"), any()))
                .thenReturn("Jane.Smith");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("xyz9876543");

        // When
        trainerService.createTrainer(trainer);

        // Then
        verify(trainerDaoImpl).save(trainer);
        assertEquals("Jane.Smith", trainer.getUsername());
        assertEquals("xyz9876543", trainer.getPassword());
        assertTrue(trainer.isActive());
    }

    @Test
    void createTrainer_NullTrainer_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.createTrainer(null)
        );
        assertEquals("Trainer must not be null", exception.getMessage());
    }

    @Test
    void createTrainer_BlankFirstName_ThrowsException() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setFirstName("");
        trainer.setLastName("Smith");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.createTrainer(trainer)
        );
        assertEquals("First name must not be blank", exception.getMessage());
    }

    @Test
    void createTrainer_BlankLastName_ThrowsException() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.createTrainer(trainer)
        );
        assertEquals("Last name must not be blank", exception.getMessage());
    }

    @Test
    void updateTrainer_Success() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("Jane.Smith");
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        // When
        trainerService.updateTrainer(trainer);

        // Then
        verify(trainerDaoImpl).update(trainer);
    }

    @Test
    void updateTrainer_NullTrainer_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer(null)
        );
        assertEquals("Trainer and trainer.id must not be null", exception.getMessage());
    }

    @Test
    void updateTrainer_NullId_ThrowsException() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setUsername("Jane.Smith");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer(trainer)
        );
        assertEquals("Trainer and trainer.id must not be null", exception.getMessage());
    }

    @Test
    void updateTrainer_BlankUsername_ThrowsException() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer(trainer)
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }

    @Test
    void selectTrainerById_Success() {
        // Given
        Long id = 1L;
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        when(trainerDaoImpl.findById(id)).thenReturn(expectedTrainer);

        // When
        Trainer result = trainerService.selectTrainerById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(trainerDaoImpl).findById(id);
    }

    @Test
    void selectTrainerById_NullId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectTrainerById(null)
        );
        assertEquals("Id must not be null", exception.getMessage());
    }

    @Test
    void selectTrainerByUsername_Success() {
        // Given
        String username = "Jane.Smith";
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setUsername(username);
        when(trainerDaoImpl.findByUsername(username)).thenReturn(expectedTrainer);

        // When
        Trainer result = trainerService.selectTrainerByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(trainerDaoImpl).findByUsername(username);
    }

    @Test
    void selectTrainerByUsername_BlankUsername_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectTrainerByUsername("")
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }
}
