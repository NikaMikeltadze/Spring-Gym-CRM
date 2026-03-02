package com.gym.crm.facade.impl;

import com.gym.crm.dto.TraineeDTO;
import com.gym.crm.dto.TrainerDTO;
import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Training;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Validated
public class GymFacadeImpl implements GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final ModelMapper modelMapper;

    public void createTrainee(TraineeDTO trainee) {
        Trainee traineeEntity = modelMapper.map(trainee, Trainee.class);
        traineeService.createTrainee(traineeEntity);
    }

    public Optional<TraineeDTO> getTraineeByUsername(String username) {
        return traineeService.selectTraineeByUsername(username)
                .map(traineeEntity -> modelMapper.map(traineeEntity, TraineeDTO.class));
    }

    public Optional<TraineeDTO> getTraineeById(Long id) {
        return traineeService.selectTraineeById(id)
                .map(traineeEntity -> modelMapper.map(traineeEntity, TraineeDTO.class));
    }

    public void updateTrainee(TraineeDTO trainee) {
        Trainee traineeEntity = modelMapper.map(trainee, Trainee.class);
        traineeService.updateTrainee(traineeEntity);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public void createTrainer(TrainerDTO trainer) {
        Trainer trainerEntity = modelMapper.map(trainer, Trainer.class);
        trainerService.createTrainer(trainerEntity);
    }

    public Optional<TrainerDTO> getTrainerByUsername(String username) {
        return trainerService.selectTrainerByUsername(username)
                .map(trainerEntity -> modelMapper.map(trainerEntity, TrainerDTO.class));
    }

    public void updateTrainer(TrainerDTO trainer) {
        Trainer trainerEntity = modelMapper.map(trainer, Trainer.class);
        trainerService.updateTrainer(trainerEntity);
    }

    public void createTraining(TrainingDTO training) {
        Training trainingEntity = modelMapper.map(training, Training.class);
        trainingService.createTraining(trainingEntity);
    }

    public Optional<TrainingDTO> getTraining(Long id) {
        return trainingService.selectTraining(id)
                .map(trainingEntity -> modelMapper.map(trainingEntity, TrainingDTO.class));
    }

    public List<TrainingDTO> getAllTrainings() {
        return trainingService.getAllTrainings()
                .stream()
                .map(trainingEntity -> modelMapper.map(trainingEntity, TrainingDTO.class))
                .toList();
    }

}


