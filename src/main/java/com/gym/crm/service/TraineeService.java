package com.gym.crm.service;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.response.trainee.*;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.entity.Trainee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    RegisterTraineeResponse createTrainee(@Valid @NotNull Trainee trainee);

    UpdateTraineeProfileResponse updateTrainee(UpdateTraineeProfileRequest request);

    void deleteTrainee(@NotBlank String username);

    Optional<GetTraineeProfileResponse> selectTraineeByUsername(@NotBlank String username);

    Optional<GetTraineeProfileResponse> selectTraineeById(@NotNull Long id);

    void changePassword(@Valid @NotNull ChangeLoginRequest request);

    void activateTrainee(@Valid @NotBlank ActivateTraineeRequest request);

    void deactivateTrainee(@Valid @NotBlank DeactivateTraineeRequest request);

    List<GetTraineeTrainingsResponse> getTrainings(
            @Valid @NotBlank GetTraineeTrainingsRequest request
    );

    UpdateTraineeTrainerListResponse updateTrainerList(@Valid @NotBlank UpdateTraineeTrainerListRequest request);

    List<TrainerProfileInfo> getUnassignedActiveTrainers(@Valid @NotBlank TraineeAssignableTrainerRequest request);

    List<GetTrainerTrainingsResponse> getTrainerTrainings(GetTrainerTrainingsRequest request);
}
