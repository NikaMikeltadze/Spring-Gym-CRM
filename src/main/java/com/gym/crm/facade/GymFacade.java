package com.gym.crm.facade;

import com.gym.crm.dto.TraineeDTO;
import com.gym.crm.dto.TrainerDTO;
import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface GymFacade {
    void createTrainee(@Valid @NotNull TraineeDTO trainee);

    Optional<TraineeDTO> getTraineeByUsername(@NotBlank String username);

    Optional<TraineeDTO> getTraineeById(@NotNull Long id);

    void updateTrainee(@Valid @NotNull TraineeDTO trainee);

    void deleteTrainee(@NotBlank String username);

    void createTrainer(@Valid @NotNull TrainerDTO trainer);

    Optional<TrainerDTO> getTrainerByUsername(@NotBlank String username);

    void updateTrainer(@Valid @NotNull TrainerDTO trainer);

    void createTraining(@Valid @NotNull TrainingDTO training);

    Optional<TrainingDTO> getTraining(@NotNull Long id);

    List<TrainingDTO> getAllTrainings();

}

