package com.gym.crm.service;

import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Trainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TrainerService {
    void createTrainer(@Valid @NotNull Trainer trainer);

    void updateTrainer(@Valid @NotNull Trainer trainer);

    Optional<Trainer> selectTrainerById(@NotNull Long id);

    Optional<Trainer> selectTrainerByUsername(@NotBlank String username);

    void changePassword(@NotBlank String username, @NotBlank String oldPassword, @NotBlank String newPassword);

    void activateTrainer(@NotBlank String username);

    void deactivateTrainer(@NotBlank String username);

    List<TrainingDTO> getTrainings(
            @NotBlank String username,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );
}
