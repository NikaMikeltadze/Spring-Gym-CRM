package com.gym.crm.dao.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TraineeDaoImpl traineeDao;

    @Test
    void save_Success() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);
        trainee.setUser(new User());
        trainee.getUser().setUsername("Sarah.Williams");
        trainee.getUser().setFirstName("Sarah");
        trainee.getUser().setLastName("Williams");

        traineeDao.save(trainee);

        verify(entityManager).persist(trainee);
    }

    @Test
    void findById_Success() {
        Long id = 5L;
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(id);
        expectedTrainee.setUser(new User());
        expectedTrainee.getUser().setUsername("Sarah.Williams");

        when(entityManager.find(Trainee.class, id)).thenReturn(expectedTrainee);

        Optional<Trainee> result = traineeDao.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Sarah.Williams", result.get().getUser().getUsername());
        verify(entityManager).find(Trainee.class, id);
    }

    @Test
    void findById_NotFound() {
        Long id = 999L;
        when(entityManager.find(Trainee.class, id)).thenReturn(null);

        Optional<Trainee> result = traineeDao.findById(id);

        assertFalse(result.isPresent());
        verify(entityManager).find(Trainee.class, id);
    }

    @Test
    void update_Success() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);
        trainee.setUser(new User());
        trainee.getUser().setUsername("Sarah.Williams");
        trainee.getUser().setFirstName("Sarah");

        when(entityManager.merge(trainee)).thenReturn(trainee);

        traineeDao.update(trainee);

        verify(entityManager).merge(trainee);
    }
}
