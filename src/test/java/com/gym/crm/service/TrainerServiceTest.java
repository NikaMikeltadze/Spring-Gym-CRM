package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.service.impl.TrainerServiceImpl;
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
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;



    @Mock
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("Jane"), eq("Smith"), any()))
                .thenReturn("Jane.Smith");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("xyz9876543");

        trainerService.createTrainer(trainer);

        verify(trainerDao).save(trainer);
        assertEquals("Jane.Smith", trainer.getUsername());
        assertEquals("xyz9876543", trainer.getPassword());
        assertTrue(trainer.getIsActive());
    }

    @Test
    void updateTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("Jane.Smith");
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        trainerService.updateTrainer(trainer);

        verify(trainerDao).update(trainer);
    }

    @Test
    void selectTrainerById_Success() {
        Long id = 1L;
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        when(trainerDao.findById(id)).thenReturn(expectedTrainer);

        Optional<Trainer> result = trainerService.selectTrainerById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(trainerDao).findById(id);
    }

    @Test
    void selectTrainerByUsername_Success() {
        String username = "Jane.Smith";
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setUsername(username);
        when(trainerDao.findByUsername(username)).thenReturn(expectedTrainer);

        Optional<Trainer> result = trainerService.selectTrainerByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(trainerDao).findByUsername(username);
    }

    @Test
    void selectTrainerByUsername_NotFound() {
        String username = "NonExistent.Trainer";
        when(trainerDao.findByUsername(username)).thenReturn(null);

        Optional<Trainer> result = trainerService.selectTrainerByUsername(username);

        assertFalse(result.isPresent());
        verify(trainerDao).findByUsername(username);
    }

    @Test
    void selectTrainerById_NotFound() {
        Long id = 999L;
        when(trainerDao.findById(id)).thenReturn(null);

        Optional<Trainer> result = trainerService.selectTrainerById(id);

        assertFalse(result.isPresent());
        verify(trainerDao).findById(id);
    }

    @Test
    void createTrainer_WithDuplicateUsername_GeneratesSerialNumber() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        when(usernamePasswordGenerator.generateUsername(eq("Jane"), eq("Smith"), any()))
                .thenReturn("Jane.Smith1");
        when(usernamePasswordGenerator.generatePassword()).thenReturn("xyz9876543");

        trainerService.createTrainer(trainer);

        verify(trainerDao).save(trainer);
        assertEquals("Jane.Smith1", trainer.getUsername());
        assertEquals("xyz9876543", trainer.getPassword());
        assertTrue(trainer.getIsActive());
    }
}
