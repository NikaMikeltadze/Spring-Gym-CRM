package com.gym.crm.health;

import com.gym.crm.dao.TrainerDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainersHealthIndicatorTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainersHealthIndicator indicator;

    @Test
    void health_ReturnsUp_WhenTrainersExist() {
        when(trainerDao.countAll()).thenReturn(3L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(3L, health.getDetails().get("trainersCount"));
    }

    @Test
    void health_ReturnsDown_WhenNoTrainersExist() {
        when(trainerDao.countAll()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(0L, health.getDetails().get("trainersCount"));
    }

    @Test
    void health_ReturnsDown_WhenDaoThrowsException() {
        when(trainerDao.countAll()).thenThrow(new IllegalStateException("DB error"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Cannot read trainers", health.getDetails().get("reason"));
    }
}
