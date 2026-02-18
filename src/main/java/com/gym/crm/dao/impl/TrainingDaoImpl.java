package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TrainingDaoImpl implements TrainingDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainingDaoImpl.class);

    private final Map<Long, Training> trainingStorage;

    public TrainingDaoImpl(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void save(Training training) {
        if (training.getId() == null) training.setId(trainingStorage.size() + 1L);
        trainingStorage.put(training.getId(), training);
        logger.debug("Saved training with id={}, name={}", training.getId(), training.getTrainingName());
    }

    @Override
    public Training findById(Long id) {
        Training training = trainingStorage.get(id);
        logger.debug("Finding training by id={}, found: {}", id, training != null);
        return training;
    }

    @Override
    public List<Training> findAll() {
        List<Training> trainings = trainingStorage.values().stream().toList();
        logger.debug("Finding all trainings, count: {}", trainings.size());
        return trainings;
    }
}

