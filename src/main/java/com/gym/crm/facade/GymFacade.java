package com.gym.crm.facade;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.trainee.*;
import com.gym.crm.dto.response.trainer.*;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface GymFacade {
    RegisterTraineeResponse createTrainee(@Valid @NotNull RegisterTraineeRequest traineeRequest);

    RegisterTrainerResponse createTrainer(@Valid @NotNull RegisterTrainerRequest trainerRequest);

    void createTraining(@Valid @NotNull AddTrainingRequest training);

    GetTraineeProfileResponse getTraineeByUsername(@NotBlank String username);

    Optional<GetTraineeProfileResponse> getTraineeById(@NotNull Long id);

    Optional<GetTrainerProfileResponse> getTrainerByUsername(@NotBlank String username);

    GetTrainingTypesResponse getAllTrainings();

    UpdateTraineeProfileResponse updateTrainee(@Valid @NotNull UpdateTraineeProfileRequest request);

    UpdateTrainerProfileResponse updateTrainer(@Valid @NotNull UpdateTrainerProfileRequest request);

    void deleteTrainee(@NotBlank String username);

    void changeUserPassword(@Valid @NotNull ChangeLoginRequest request);

    void activateTrainee(@Valid @NotNull ActivateTraineeRequest request);

    void deactivateTrainee(@Valid @NotNull DeactivateTraineeRequest request);

    void activateTrainer(@NotBlank String username);

    void deactivateTrainer(@NotBlank String username);

    List<GetTraineeTrainingsResponse> getTraineeTrainings(
            @Valid @NotNull GetTraineeTrainingsRequest request
    );

    UpdateTraineeTrainerListResponse updateTrainerList(@Valid @NotNull UpdateTraineeTrainerListRequest request);

    List<TrainerProfileInfo> getUnassignedActiveTrainers(@NotBlank String traineeUsername);

    List<GetTrainerTrainingsResponse> getTrainerTrainings(@Valid GetTrainerTrainingsRequest request);
}

