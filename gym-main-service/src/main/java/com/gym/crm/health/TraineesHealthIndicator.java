package com.gym.crm.health;

import com.gym.crm.dao.TraineeDao;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TraineesHealthIndicator implements HealthIndicator {

    private final TraineeDao traineeDao;

    public TraineesHealthIndicator(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Health health() {
        try {
            long traineesCount = traineeDao.countAll();
            if (traineesCount == 0L) {
                return Health.down()
                        .withDetail("reason", "No trainees are loaded")
                        .withDetail("traineesCount", traineesCount)
                        .build();
            }

            return Health.up()
                    .withDetail("traineesCount", traineesCount)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("reason", "Cannot read trainees")
                    .build();
        }
    }
}
