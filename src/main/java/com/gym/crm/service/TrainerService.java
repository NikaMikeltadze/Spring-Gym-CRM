package com.gym.crm.service;

import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.entity.Trainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;


public interface TrainerService {
    RegisterTrainerResponse createTrainer(@Valid @NotNull Trainer trainer);

    void updateTrainer(@Valid @NotNull Trainer trainer);

    Optional<Trainer> selectTrainerById(@NotNull Long id);

    Optional<Trainer> selectTrainerByUsername(@NotBlank String username);

    void changePassword(@NotBlank String username, @NotBlank String oldPassword, @NotBlank String newPassword);

    void activateTrainer(@NotBlank String username);

    void deactivateTrainer(@NotBlank String username);

    GetTrainingTypesResponse getTrainings(
            @NotBlank String username,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );
}
