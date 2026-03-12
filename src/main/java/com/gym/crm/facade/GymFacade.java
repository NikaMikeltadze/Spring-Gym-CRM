package com.gym.crm.facade;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface GymFacade {
    RegisterTraineeResponse createTrainee(@Valid @NotNull Trainee trainee);

    RegisterTrainerResponse createTrainer(@Valid @NotNull Trainer trainer);

    void createTraining(@Valid @NotNull AddTrainingRequest training);

    Optional<GetTraineeProfileResponse> getTraineeByUsername(@NotBlank String username);

    Optional<GetTraineeProfileResponse> getTraineeById(@NotNull Long id);

    Optional<GetTrainerProfileResponse> getTrainerByUsername(@NotBlank String username);

    GetTrainingTypesResponse getAllTrainings();

    UpdateTraineeProfileResponse updateTrainee(@Valid @NotNull Trainee trainee);

    UpdateTrainerProfileResponse updateTrainer(@Valid @NotNull Trainer trainer);

    void deleteTrainee(@NotBlank String username);

    void changeTraineePassword(@Valid @NotNull ChangeLoginRequest request);

    void changeTrainerPassword(@NotBlank String username, @NotBlank String oldPassword, @NotBlank String newPassword);

    void activateTrainee(@Valid @NotNull ActivateTraineeRequest request);

    void deactivateTrainee(@Valid @NotNull DeactivateTraineeRequest request);

    void activateTrainer(@NotBlank String username);

    void deactivateTrainer(@NotBlank String username);

    List<GetTraineeTrainingsResponse> getTraineeTrainings(
            @Valid @NotNull GetTraineeTrainingsRequest request
    );


    List<TrainerProfileInfo> updateTrainerList(@Valid @NotNull UpdateTraineeTrainerListRequest request);

    List<TrainerProfileInfo> getUnassignedActiveTrainers(@NotBlank String traineeUsername);
}

