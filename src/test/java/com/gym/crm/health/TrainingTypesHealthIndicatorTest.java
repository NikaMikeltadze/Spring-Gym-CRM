package com.gym.crm.health;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.entity.TrainingType;
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
class TrainingTypesHealthIndicatorTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypesHealthIndicator indicator;

    @Test
    void health_ReturnsUp_WhenTrainingTypesExist() {
        when(trainingTypeDao.findAll()).thenReturn(List.of(new TrainingType(), new TrainingType()));

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().get("trainingTypesCount"));
    }

    @Test
    void health_ReturnsDown_WhenTrainingTypesAreEmpty() {
        when(trainingTypeDao.findAll()).thenReturn(List.of());

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(0, health.getDetails().get("trainingTypesCount"));
    }
}
