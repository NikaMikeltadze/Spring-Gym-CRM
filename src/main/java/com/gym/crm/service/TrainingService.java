package com.gym.crm.service;

import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;


public interface TrainingService {
    TrainingTypeInfo createTraining(@Valid @NotNull AddTrainingRequest training);

    Optional<TrainingTypeInfo> selectTraining(@NotNull Long id);

    GetTrainingTypesResponse getAllTrainings();

    GetTrainingTypesResponse findTrainingsByDateRange(LocalDate fromDate, LocalDate toDate);
}
