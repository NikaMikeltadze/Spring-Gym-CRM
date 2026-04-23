package com.gym.crm.service;

import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.trainer.GetTrainerMonthlyWorkloadResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;


public interface TrainingService {
    TrainingTypeInfo createTraining(@Valid @NotNull AddTrainingRequest training);

    void deleteTraining(@NotNull Long id);

    GetTrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(@NotNull String trainerUsername,
                                                                @NotNull Integer year,
                                                                @NotNull Integer month);

    Optional<TrainingTypeInfo> selectTraining(@NotNull Long id);

    GetTrainingTypesResponse getAllTrainings();

    GetTrainingTypesResponse findTrainingsByDateRange(LocalDate fromDate, LocalDate toDate);
}
