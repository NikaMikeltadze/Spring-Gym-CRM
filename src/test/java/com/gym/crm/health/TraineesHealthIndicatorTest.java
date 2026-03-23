package com.gym.crm.health;

import com.gym.crm.dao.TraineeDao;
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
class TraineesHealthIndicatorTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineesHealthIndicator indicator;

    @Test
    void health_ReturnsUp_WhenTraineesExist() {
        when(traineeDao.countAll()).thenReturn(5L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(5L, health.getDetails().get("traineesCount"));
    }

    @Test
    void health_ReturnsDown_WhenNoTraineesExist() {
        when(traineeDao.countAll()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(0L, health.getDetails().get("traineesCount"));
    }

    @Test
    void health_ReturnsDown_WhenDaoThrowsException() {
        when(traineeDao.countAll()).thenThrow(new IllegalStateException("DB error"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Cannot read trainees", health.getDetails().get("reason"));
    }
}
