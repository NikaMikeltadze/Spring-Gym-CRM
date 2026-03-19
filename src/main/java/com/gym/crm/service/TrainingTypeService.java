package com.gym.crm.service;

import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface TrainingTypeService {
    Optional<TrainingTypeInfo> getById(@NotNull Long id);

    Optional<GetTrainingTypesResponse> getAllTrainingTypes();
}
