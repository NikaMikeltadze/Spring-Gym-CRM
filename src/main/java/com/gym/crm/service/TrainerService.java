package com.gym.crm.service;

import com.gym.crm.model.Trainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;


public interface TrainerService {
    void createTrainer(@Valid @NotNull Trainer trainer);

    void updateTrainer(@Valid @NotNull Trainer trainer);

    Optional<Trainer> selectTrainerById(@NotNull Long id);

    Optional<Trainer> selectTrainerByUsername(@NotBlank String username);
}

