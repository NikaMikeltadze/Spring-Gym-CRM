package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacade {


    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    //Trainee
    public void createTrainee(Trainee trainee) {
        traineeService.createTrainee(trainee);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.selectTraineeByUsername(username);
    }

    public Trainee getTraineeById(Long id) {
        return traineeService.selectTraineeById(id);
    }

    public void updateTrainee(Trainee trainee) {
        traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    //Trainer
    public void createTrainer(Trainer trainer) {
        trainerService.createTrainer(trainer);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.selectTrainerByUsername(username);
    }

    public void updateTrainer(Trainer trainer) {
        trainerService.updateTrainer(trainer);
    }

    //training
    public void createTraining(Training training) {
        trainingService.createTraining(training);
    }

    public Training getTraining(Long id) {
        return trainingService.selectTraining(id);
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

}

