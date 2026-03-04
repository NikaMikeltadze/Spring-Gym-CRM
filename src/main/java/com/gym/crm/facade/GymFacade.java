package com.gym.crm.facade;

import com.gym.crm.dto.TraineeDTO;
import com.gym.crm.dto.TrainerDTO;
import com.gym.crm.dto.TrainingDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public interface GymFacade {
    void createTrainee(@Valid @NotNull TraineeDTO trainee);

    void createTrainer(@Valid @NotNull TrainerDTO trainer);

    void createTraining(@Valid @NotNull TrainingDTO training);

    Optional<TraineeDTO> getTraineeByUsername(@NotBlank String username);

    Optional<TraineeDTO> getTraineeById(@NotNull Long id);

    Optional<TrainerDTO> getTrainerByUsername(@NotBlank String username);

    Optional<TrainingDTO> getTraining(@NotNull Long id);

    List<TrainingDTO> getAllTrainings();

    void updateTrainee(@Valid @NotNull TraineeDTO trainee);

    void updateTrainer(@Valid @NotNull TrainerDTO trainer);

    void deleteTrainee(@NotBlank String username);

    void changeTraineePassword(@NotBlank String username, @NotBlank String oldPassword, @NotBlank String newPassword);

    void changeTrainerPassword(@NotBlank String username, @NotBlank String oldPassword, @NotBlank String newPassword);

    void activateTrainee(@NotBlank String username);

    void deactivateTrainee(@NotBlank String username);

    void activateTrainer(@NotBlank String username);

    void deactivateTrainer(@NotBlank String username);

    List<TrainingDTO> getTraineeTrainings(
            @NotBlank String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    );

    List<TrainingDTO> getTrainerTrainings(
            @NotBlank String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );
}

