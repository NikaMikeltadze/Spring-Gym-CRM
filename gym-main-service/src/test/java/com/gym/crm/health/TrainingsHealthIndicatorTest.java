package com.gym.crm.health;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingsHealthIndicatorTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingsHealthIndicator indicator;

    @Test
    void health_ReturnsUp_WhenTrainingsExist() {
        when(trainingDao.findAll()).thenReturn(List.of(new Training()));

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(1, health.getDetails().get("trainingsCount"));
    }

    @Test
    void health_ReturnsDown_WhenTrainingsAreEmpty() {
        when(trainingDao.findAll()).thenReturn(List.of());

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(0, health.getDetails().get("trainingsCount"));
    }
}
