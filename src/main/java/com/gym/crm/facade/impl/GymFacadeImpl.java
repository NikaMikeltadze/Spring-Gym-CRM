package com.gym.crm.facade.impl;

import com.gym.crm.facade.GymFacade;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GymFacadeImpl implements GymFacade {
    //TODO: DTOs And ModelMapper
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public void createTrainee(Trainee trainee) {
        traineeService.createTrainee(trainee);
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        return traineeService.selectTraineeByUsername(username);
    }

    public Optional<Trainee> getTraineeById(Long id) {
        return traineeService.selectTraineeById(id);
    }

    public void updateTrainee(Trainee trainee) {
        traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public void createTrainer(Trainer trainer) {
        trainerService.createTrainer(trainer);
    }

    public Optional<Trainer> getTrainerByUsername(String username) {
        return trainerService.selectTrainerByUsername(username);
    }

    public void updateTrainer(Trainer trainer) {
        trainerService.updateTrainer(trainer);
    }

    public void createTraining(Training training) {
        trainingService.createTraining(training);
    }

    public Optional<Training> getTraining(Long id) {
        return trainingService.selectTraining(id);
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

}


