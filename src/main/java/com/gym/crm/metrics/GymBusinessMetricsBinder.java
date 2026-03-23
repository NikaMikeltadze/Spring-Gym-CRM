package com.gym.crm.metrics;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymBusinessMetricsBinder implements MeterBinder {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("gym.trainees.total", this::safeTraineesCount)
                .description("Total number of trainees")
                .register(registry);

        Gauge.builder("gym.trainers.total", this::safeTrainersCount)
                .description("Total number of trainers")
                .register(registry);

        Gauge.builder("gym.trainings.total", this::safeTrainingsCount)
                .description("Total number of trainings")
                .register(registry);
    }

    private double safeTraineesCount() {
        try {
            return traineeDao.countAll();
        } catch (Exception ex) {
            log.warn("Failed to read trainees count for metrics", ex);
            return Double.NaN;
        }
    }

    private double safeTrainersCount() {
        try {
            return trainerDao.countAll();
        } catch (Exception ex) {
            log.warn("Failed to read trainers count for metrics", ex);
            return Double.NaN;
        }
    }

    private double safeTrainingsCount() {
        try {
            List<?> trainings = trainingDao.findAll();
            return trainings.size();
        } catch (Exception ex) {
            log.warn("Failed to read trainings count for metrics", ex);
            return Double.NaN;
        }
    }
}
