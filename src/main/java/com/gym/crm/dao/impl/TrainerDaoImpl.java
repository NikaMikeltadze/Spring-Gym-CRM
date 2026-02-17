package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainerDaoImpl implements TrainerDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDaoImpl.class);

    private final Map<Long, Trainer> trainerStorage;

    public TrainerDaoImpl(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void save(Trainer trainer) {
        if(trainer.getId() == null) trainer.setId(trainerStorage.size() + 1L);
        trainerStorage.put(trainer.getId(), trainer);
        logger.debug("Saved trainer with id={}, username={}", trainer.getId(), trainer.getUsername());
    }

    @Override
    public Trainer findByUsername(String username) {
        Trainer trainer = trainerStorage
                .values()
                .stream()
                .filter(t -> username.equals(t.getUsername()))
                .findFirst().orElse(null);
        logger.debug("Finding trainer by username={}, found: {}", username, trainer != null);
        return trainer;
    }

    @Override
    public Trainer findById(Long id) {
        Trainer trainer = trainerStorage.get(id);
        logger.debug("Finding trainer by id={}, found: {}", id, trainer != null);
        return trainer;
    }

    @Override
    public void update(Trainer trainer) {
        trainerStorage.put(trainer.getId(), trainer);
        logger.debug("Updated trainer with id={}, username={}", trainer.getId(), trainer.getUsername());
    }

    @Override
    public boolean exists(String username) {
        boolean exists = trainerStorage
                .values()
                .stream()
                .anyMatch(t -> t.getUsername() != null && t.getUsername().equals(username));
        logger.debug("Checking if trainer exists with username={}, exists: {}", username, exists);
        return exists;
    }
}
