package com.gym.crm.service;

import com.gym.crm.model.Trainee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface TraineeService {
    void createTrainee(@Valid @NotNull Trainee trainee);

    void updateTrainee(@Valid @NotNull Trainee trainee);

    void deleteTrainee(@NotBlank String username);

    Optional<Trainee> selectTraineeByUsername(@NotBlank String username);

    Optional<Trainee> selectTraineeById(@NotNull Long id);

}
