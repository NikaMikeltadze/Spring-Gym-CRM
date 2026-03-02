package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;

    @Override
    @Transactional
    public void createTraining(Training training) {
        log.debug("Creating training with name={}", training.getTrainingName());
        trainingDao.save(training);
        log.info("Successfully created training with name={}", training.getTrainingName());
    }

    public Optional<Training> selectTraining(Long id) {
        log.debug("Selecting training by id={}", id);
        Training training = trainingDao.findById(id);
        log.debug("Found training: {}", training != null);
        return Optional.ofNullable(training);
    }

    public List<Training> getAllTrainings() {
        log.debug("Retrieving all trainings");
        List<Training> trainings = trainingDao.findAll();
        log.debug("Found {} trainings", trainings.size());
        return trainings;
    }
}
