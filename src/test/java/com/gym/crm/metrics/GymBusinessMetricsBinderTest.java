package com.gym.crm.metrics;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymBusinessMetricsBinderTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Test
    void bindTo_RegistersBusinessGaugeMetrics() {
        when(traineeDao.countAll()).thenReturn(10L);
        when(trainerDao.countAll()).thenReturn(5L);
        when(trainingDao.countAll()).thenReturn(3L);

        GymBusinessMetricsBinder binder = new GymBusinessMetricsBinder(traineeDao, trainerDao, trainingDao);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

        binder.bindTo(meterRegistry);

        assertEquals(10.0, meterRegistry.get("gym.trainees.total").gauge().value());
        assertEquals(5.0, meterRegistry.get("gym.trainers.total").gauge().value());
        assertEquals(3.0, meterRegistry.get("gym.trainings.total").gauge().value());
    }

    @Test
    void bindTo_ReturnsNaN_WhenMetricSourceFails() {
        when(traineeDao.countAll()).thenThrow(new RuntimeException("DB unavailable"));
        when(trainerDao.countAll()).thenThrow(new RuntimeException("DB unavailable"));
        when(trainingDao.countAll()).thenThrow(new RuntimeException("DB unavailable"));

        GymBusinessMetricsBinder binder = new GymBusinessMetricsBinder(traineeDao, trainerDao, trainingDao);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

        binder.bindTo(meterRegistry);

        assertTrue(Double.isNaN(meterRegistry.get("gym.trainees.total").gauge().value()));
        assertTrue(Double.isNaN(meterRegistry.get("gym.trainers.total").gauge().value()));
        assertTrue(Double.isNaN(meterRegistry.get("gym.trainings.total").gauge().value()));
    }
}
