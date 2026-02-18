package com.gym.crm.service;

import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.dao.impl.TrainerDaoImpl;
import com.gym.crm.dao.impl.TrainingDaoImpl;
import com.gym.crm.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainingService {

    private final TrainingDaoImpl trainingDaoImpl;
    private final TraineeDaoImpl traineeDaoImpl;
    private final TrainerDaoImpl trainerDaoImpl;

    public TrainingService(TrainingDaoImpl trainingDaoImpl, TraineeDaoImpl traineeDaoImpl, TrainerDaoImpl trainerDaoImpl) {
        this.trainingDaoImpl = trainingDaoImpl;
        this.traineeDaoImpl = traineeDaoImpl;
        this.trainerDaoImpl = trainerDaoImpl;
    }

    public void createTraining(Training training) {
        log.debug("Creating training with name={}", training != null ? training.getTrainingName() : null);

        if (training == null) {
            log.error("Attempted to create null training");
            throw new IllegalArgumentException("Training must not be null");
        }
        if (training.getTrainingName() == null || training.getTrainingName().trim().isEmpty()) {
            log.error("Attempted to create training with blank name");
            throw new IllegalArgumentException("Training name must not be blank");
        }
        if (training.getTrainingType() == null) {
            log.error("Attempted to create training with null type");
            throw new IllegalArgumentException("Training type must not be null");
        }
        if (training.getTrainerId() == null) {
            log.error("Attempted to create training with null trainerId");
            throw new IllegalArgumentException("TrainerId must not be null");
        }
        if (training.getTraineeId() == null) {
            log.error("Attempted to create training with null traineeId");
            throw new IllegalArgumentException("TraineeId must not be null");
        }

        if (trainerDaoImpl.findById(training.getTrainerId()) == null) {
            log.error("Trainer with id={} does not exist", training.getTrainerId());
            throw new IllegalArgumentException("Trainer with id=" + training.getTrainerId() + " does not exist");
        }
        if (traineeDaoImpl.findById(training.getTraineeId()) == null) {
            log.error("Trainee with id={} does not exist", training.getTraineeId());
            throw new IllegalArgumentException("Trainee with id=" + training.getTraineeId() + " does not exist");
        }

        trainingDaoImpl.save(training);
        log.info("Successfully created training with name={}", training.getTrainingName());
    }

    public Training selectTraining(Long id) {
        log.debug("Selecting training by id={}", id);

        Training training = trainingDaoImpl.findById(id);
        log.debug("Found training: {}", training != null);
        return training;
    }

    public List<Training> getAllTrainings() {
        log.debug("Retrieving all trainings");

        List<Training> trainings = trainingDaoImpl.findAll();
        log.debug("Found {} trainings", trainings.size());
        return trainings;
    }
}

