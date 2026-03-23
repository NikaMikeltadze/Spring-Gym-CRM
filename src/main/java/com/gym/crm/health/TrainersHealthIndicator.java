package com.gym.crm.health;

import com.gym.crm.dao.TrainerDao;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainersHealthIndicator implements HealthIndicator {

    private final TrainerDao trainerDao;

    public TrainersHealthIndicator(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Health health() {
        try {
            long trainersCount = trainerDao.countAll();
            if (trainersCount == 0L) {
                return Health.down()
                        .withDetail("reason", "No trainers are loaded")
                        .withDetail("trainersCount", trainersCount)
                        .build();
            }

            return Health.up()
                    .withDetail("trainersCount", trainersCount)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("reason", "Cannot read trainers")
                    .build();
        }
    }
}
