package com.gym.crm.health;

import com.gym.crm.dao.TrainingTypeDao;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypesHealthIndicator implements HealthIndicator {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypesHealthIndicator(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Health health() {
        try {
            int trainingTypesCount = trainingTypeDao.findAll().size();
            if (trainingTypesCount == 0) {
                return Health.down()
                        .withDetail("reason", "No training types are loaded")
                        .withDetail("trainingTypesCount", trainingTypesCount)
                        .build();
            }

            return Health.up()
                    .withDetail("trainingTypesCount", trainingTypesCount)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("reason", "Cannot read training types")
                    .build();
        }
    }
}
