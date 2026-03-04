package com.gym.crm.dao.impl;

import com.gym.crm.entity.Trainer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {

    @Mock
    private EntityManager entityManager;

    private TrainerDaoImpl trainerDao;

    @BeforeEach
    void setUp() {
        trainerDao = new TrainerDaoImpl();
        trainerDao.setEntityManager(entityManager);
    }

    @Test
    void save_Success() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("Jane.Smith");
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        trainerDao.save(trainer);

        verify(entityManager).persist(trainer);
    }


    @Test
    void findById_Success() {
        Long id = 1L;
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        expectedTrainer.setUsername("Jane.Smith");

        when(entityManager.find(Trainer.class, id)).thenReturn(expectedTrainer);
        Trainer result = trainerDao.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Jane.Smith", result.getUsername());
        verify(entityManager).find(Trainer.class, id);
    }

    @Test
    void findById_NotFound() {
        Long id = 999L;
        when(entityManager.find(Trainer.class, id)).thenReturn(null);

        Trainer result = trainerDao.findById(id);

        assertNull(result);
        verify(entityManager).find(Trainer.class, id);
    }

    @Test
    void update_Success() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("Jane.Smith");
        trainer.setFirstName("Jane");

        when(entityManager.merge(trainer)).thenReturn(trainer);

        trainerDao.update(trainer);

        verify(entityManager).merge(trainer);
    }

}
