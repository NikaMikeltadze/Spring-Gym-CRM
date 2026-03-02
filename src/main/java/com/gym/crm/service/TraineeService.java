package com.gym.crm.service;

import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Trainee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeService {
    void createTrainee(@Valid @NotNull Trainee trainee);

    void updateTrainee(@Valid @NotNull Trainee trainee);

    void deleteTrainee(@NotBlank String username);

    Optional<Trainee> selectTraineeByUsername(@NotBlank String username);

    Optional<Trainee> selectTraineeById(@NotNull Long id);

    void changePassword(@NotBlank String username, @NotBlank String oldPassword, @NotBlank String newPassword);

    void activateTrainee(@NotBlank String username);

    void deactivateTrainee(@NotBlank String username);

    List<TrainingDTO> getTrainings(
            @NotBlank String username,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    );
}
