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
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("Jane"), eq("Smith"), any()))
                .thenReturn("Jane.Smith");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("xyz9876543");

        trainerService.createTrainer(trainer);

        verify(trainerDaoImpl).save(trainer);
        assertEquals("Jane.Smith", trainer.getUsername());
        assertEquals("xyz9876543", trainer.getPassword());
        assertTrue(trainer.isActive());
    }

    @Test
    void createTrainer_NullTrainer_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.createTrainer(null)
        );
        assertEquals("Trainer must not be null", exception.getMessage());
    }

    @Test
    void createTrainer_BlankFirstName_ThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("");
        trainer.setLastName("Smith");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.createTrainer(trainer)
        );
        assertEquals("First name must not be blank", exception.getMessage());
    }

    @Test
    void createTrainer_BlankLastName_ThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.createTrainer(trainer)
        );
        assertEquals("Last name must not be blank", exception.getMessage());
    }

    @Test
    void updateTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("Jane.Smith");
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        trainerService.updateTrainer(trainer);

        verify(trainerDaoImpl).update(trainer);
    }

    @Test
    void updateTrainer_NullTrainer_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer(null)
        );
        assertEquals("Trainer and trainer.id must not be null", exception.getMessage());
    }

    @Test
    void updateTrainer_NullId_ThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Jane.Smith");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer(trainer)
        );
        assertEquals("Trainer and trainer.id must not be null", exception.getMessage());
    }

    @Test
    void updateTrainer_BlankUsername_ThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer(trainer)
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }

    @Test
    void selectTrainerById_Success() {
        Long id = 1L;
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        when(trainerDaoImpl.findById(id)).thenReturn(expectedTrainer);

        Trainer result = trainerService.selectTrainerById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(trainerDaoImpl).findById(id);
    }

    @Test
    void selectTrainerById_NullId_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectTrainerById(null)
        );
        assertEquals("Id must not be null", exception.getMessage());
    }

    @Test
    void selectTrainerByUsername_Success() {
        String username = "Jane.Smith";
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setUsername(username);
        when(trainerDaoImpl.findByUsername(username)).thenReturn(expectedTrainer);

        Trainer result = trainerService.selectTrainerByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(trainerDaoImpl).findByUsername(username);
    }

    @Test
    void selectTrainerByUsername_BlankUsername_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectTrainerByUsername("")
        );
        assertEquals("Username must not be blank", exception.getMessage());
    }
}

