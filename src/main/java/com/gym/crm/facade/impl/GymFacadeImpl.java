package com.gym.crm.facade.impl;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.trainee.*;
import com.gym.crm.dto.response.trainer.*;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
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
    private final TrainerMapper trainerMapper;
    private final UserService userService;

    @Override
    public RegisterTraineeResponse createTrainee(RegisterTraineeRequest trainee) {
        return traineeService.createTrainee(traineeMapper.toEntity(trainee));
    }

    @Override
    public GetTraineeProfileResponse getTraineeByUsername(String username) {
        return traineeService.selectTraineeByUsername(username).orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + username));
    }

    @Override
    public Optional<GetTraineeProfileResponse> getTraineeById(Long id) {
        return traineeService.selectTraineeById(id);
    }

    @Override
    public UpdateTraineeProfileResponse updateTrainee(UpdateTraineeProfileRequest request) {
        return traineeService.updateTrainee(request);
    }

    @Override
    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    @Override
    public RegisterTrainerResponse createTrainer(RegisterTrainerRequest trainerRequest) {
        return trainerService.createTrainer(trainerMapper.toEntity(trainerRequest));
    }

    @Override
    public Optional<GetTrainerProfileResponse> getTrainerByUsername(String username) {
        return trainerService.selectTrainerByUsername(username)
                .map(this::mapToGetTrainerProfileResponse);
    }

    @Override
    public UpdateTrainerProfileResponse updateTrainer(UpdateTrainerProfileRequest request) {
        return trainerService.updateTrainer(request);
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
    public void changeUserPassword(ChangeLoginRequest request) {
        userService.changePassword(request);
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
    public UpdateTraineeTrainerListResponse updateTrainerList(UpdateTraineeTrainerListRequest request) {
        return traineeService.updateTrainerList(request);
    }

    @Override
    public List<TrainerProfileInfo> getUnassignedActiveTrainers(String traineeUsername) {
        com.gym.crm.dto.request.trainee.TraineeAssignableTrainerRequest request =
                new com.gym.crm.dto.request.trainee.TraineeAssignableTrainerRequest(traineeUsername);
        request.setUsername(traineeUsername);
        return traineeService.getUnassignedActiveTrainers(request);
    }

    @Override
    public List<GetTrainerTrainingsResponse> getTrainerTrainings(GetTrainerTrainingsRequest request) {
        return traineeService.getTrainerTrainings(request);
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


