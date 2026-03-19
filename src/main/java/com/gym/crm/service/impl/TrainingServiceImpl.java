package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.mapper.TrainingTypeMapper;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    @Transactional
    public TrainingTypeInfo createTraining(AddTrainingRequest trainingRequest) {
        log.debug("Creating training with name={}", trainingRequest.getTrainingName());
        Trainee trainee = traineeDao.findByUsername(trainingRequest.getTraineeUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + trainingRequest.getTraineeUsername()));
        Trainer trainer = trainerDao.findByUsername(trainingRequest.getTrainerUsername()).orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + trainingRequest.getTrainerUsername()));
        String trainingName = trainingRequest.getTrainingName();
        TrainingType trainingType = trainingTypeDao.findByName(trainingName).orElseThrow(() -> new IllegalArgumentException("Training type not found with name: " + trainingName));
        LocalDate trainingDate = trainingRequest.getTrainingDate();
        Double trainingDuration = trainingRequest.getTrainingDuration();

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        trainingDao.save(training);
        log.info("Successfully created training with name={}", training.getTrainingName());

        return trainingTypeMapper.toTrainingTypeInfo(training.getTrainingType());
    }

    public Optional<TrainingTypeInfo> selectTraining(Long id) {
        log.debug("Selecting training by id={}", id);
        Training training = trainingDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + id));
        log.debug("Found training: {}", training.getTrainingName());
        TrainingTypeInfo trainingTypeInfo = trainingTypeMapper.toTrainingTypeInfo(training.getTrainingType());
        return Optional.of(trainingTypeInfo);
    }

    public GetTrainingTypesResponse getAllTrainings() {
        log.debug("Retrieving all trainings");
        List<Training> trainings = trainingDao.findAll();
        List<TrainingTypeInfo> trainingsInfo = trainings
                .stream()
                .map(training -> trainingTypeMapper.toTrainingTypeInfo(training.getTrainingType())).toList();
        log.debug("Found {} trainings", trainings.size());
        return new GetTrainingTypesResponse(trainingsInfo);
    }

    @Override
    public GetTrainingTypesResponse findTrainingsByDateRange(LocalDate fromDate, LocalDate toDate) {
        log.debug("Finding trainings by date range: from={}, to={}", fromDate, toDate);
        List<Training> trainings = trainingDao.findByDateRange(fromDate, toDate);
        List<TrainingTypeInfo> trainingsInfo = trainings
                .stream()
                .map(training -> trainingTypeMapper.toTrainingTypeInfo(training.getTrainingType())).toList();
        log.debug("Found {} trainings in date range", trainings.size());
        return new GetTrainingTypesResponse(trainingsInfo);
    }
}
