package com.gym.crm.facade.impl;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.ActivateTraineeRequest;
import com.gym.crm.dto.request.trainee.DeactivateTraineeRequest;
import com.gym.crm.dto.request.trainee.GetTraineeTrainingsRequest;
import com.gym.crm.dto.request.trainee.UpdateTraineeTrainerListRequest;
import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.GetTraineeTrainingsResponse;
import com.gym.crm.dto.response.trainee.RegisterTraineeResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
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
    private final TraineeMapper traineeMapper;

    @Override
    public RegisterTraineeResponse createTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    @Override
    public Optional<GetTraineeProfileResponse> getTraineeByUsername(String username) {
        return traineeService.selectTraineeByUsername(username);
    }

    @Override
    public Optional<GetTraineeProfileResponse> getTraineeById(Long id) {
        return traineeService.selectTraineeById(id);
    }

    @Override
    public UpdateTraineeProfileResponse updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }

    @Override
    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    @Override
    public RegisterTrainerResponse createTrainer(Trainer trainer) {
        return trainerService.createTrainer(trainer);
    }

    @Override
    public Optional<GetTrainerProfileResponse> getTrainerByUsername(String username) {
        return trainerService.selectTrainerByUsername(username)
                .map(this::mapToGetTrainerProfileResponse);
    }

    @Override
    public UpdateTrainerProfileResponse updateTrainer(Trainer trainer) {
        trainerService.updateTrainer(trainer);
        return trainerService.selectTrainerByUsername(trainer.getUsername())
                .map(this::mapToUpdateTrainerProfileResponse)
                .orElse(null);
    }

    @Override
    public void createTraining(AddTrainingRequest training) {
        trainingService.createTraining(training);
    }

    @Override
    public GetTrainingTypesResponse getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    @Override
    public void changeTraineePassword(ChangeLoginRequest request) {
        traineeService.changePassword(request);
    }

    @Override
    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public void activateTrainee(ActivateTraineeRequest request) {
        traineeService.activateTrainee(request);
    }

    @Override
    public void deactivateTrainee(DeactivateTraineeRequest request) {
        traineeService.deactivateTrainee(request);
    }

    @Override
    public void activateTrainer(String username) {
        trainerService.activateTrainer(username);
    }

    @Override
    public void deactivateTrainer(String username) {
        trainerService.deactivateTrainer(username);
    }

    @Override
    public List<GetTraineeTrainingsResponse> getTraineeTrainings(GetTraineeTrainingsRequest request) {
        return traineeService.getTrainings(request);
    }

    @Override
    public List<TrainerProfileInfo> updateTrainerList(UpdateTraineeTrainerListRequest request) {
        return traineeService.updateTrainerList(request);
    }

    @Override
    public List<TrainerProfileInfo> getUnassignedActiveTrainers(String traineeUsername) {
        com.gym.crm.dto.request.trainee.TraineeAssignableTrainerRequest request =
                new com.gym.crm.dto.request.trainee.TraineeAssignableTrainerRequest();
        request.setUsername(traineeUsername);
        return traineeService.getUnassignedActiveTrainers(request);
    }


    private GetTrainerProfileResponse mapToGetTrainerProfileResponse(Trainer trainer) {
        return GetTrainerProfileResponse.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .TrainingTypeId(trainer.getTrainingType().getId())
                .isActive(trainer.getIsActive())
                .traineeList(trainer.getTrainees()
                        .stream()
                        .map(traineeMapper::toGetProfileResponse)
                        .toList()
                )
                .build();
    }

    private UpdateTrainerProfileResponse mapToUpdateTrainerProfileResponse(Trainer trainer) {
        return UpdateTrainerProfileResponse.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .trainingTypeId(trainer.getTrainingType().getId())
                .isActive(trainer.getIsActive())
                .traineeList(trainer.getTrainees()
                        .stream()
                        .map(traineeMapper::toProfileInfo)
                        .toList()
                )
                .build();
    }
}


