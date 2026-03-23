package com.gym.crm.health;

import com.gym.crm.dao.TrainingDao;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingsHealthIndicator implements HealthIndicator {

    private final TrainingDao trainingDao;

    public TrainingsHealthIndicator(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Health health() {
        try {
            int trainingsCount = trainingDao.findAll().size();
            if (trainingsCount == 0) {
                return Health.down()
                        .withDetail("reason", "No trainings are loaded")
                        .withDetail("trainingsCount", trainingsCount)
                        .build();
            }

            return Health.up()
                    .withDetail("trainingsCount", trainingsCount)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("reason", "Cannot read trainings")
                    .build();
        }
    }
}
