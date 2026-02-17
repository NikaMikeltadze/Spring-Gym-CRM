package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TraineeDaoImpl implements TraineeDao {
    private static final Logger logger = LoggerFactory.getLogger(TraineeDaoImpl.class);

    private final Map<Long, Trainee> traineeStorage;

    public TraineeDaoImpl(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public void save(Trainee trainee) {
        if (trainee.getId() == null) trainee.setId(traineeStorage.size() + 1L);
        traineeStorage.put(trainee.getId(), trainee);
        logger.debug("Saved trainee with id={}, username={}", trainee.getId(), trainee.getUsername());
    }

    @Override
    public Trainee findById(Long id) {
        Trainee trainee = traineeStorage.get(id);
        logger.debug("Finding trainee by id={}, found: {}", id, trainee != null);
        return trainee;
    }

    @Override
    public Trainee findByUsername(String username) {
        Trainee trainee = traineeStorage.values()
                .stream()
                .filter(t -> t.getUsername().equals(username))
                .findFirst().orElse(null);
        logger.debug("Finding trainee by username={}, found: {}", username, trainee != null);
        return trainee;
    }

    @Override
    public void delete(String username) {
        Trainee trainee = findByUsername(username);
        if (trainee != null) {
            traineeStorage.remove(trainee.getId());
            logger.debug("Deleted trainee with username={}", username);
        }
    }

    @Override
    public void update(Trainee trainee) {
        traineeStorage.put(trainee.getId(), trainee);
        logger.debug("Updated trainee with id={}, username={}", trainee.getId(), trainee.getUsername());
    }

    @Override
    public boolean exists(String username) {
        boolean exists = traineeStorage.values()
                .stream()
                .anyMatch(t -> t.getUsername() != null && t.getUsername().equals(username));
        logger.debug("Checking if trainee exists with username={}, exists: {}", username, exists);
        return exists;
    }
}
