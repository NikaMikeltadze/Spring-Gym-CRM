package com.gym.crm.service;

import com.gym.crm.entity.Training;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TrainingService {
    void createTraining(@Valid @NotNull Training training);

    Optional<Training> selectTraining(@NotNull Long id);

    List<Training> getAllTrainings();

    List<Training> findTrainingsByDateRange(LocalDate fromDate, LocalDate toDate);
}
