package com.gym.crm.dto.response.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetTrainingTypesResponse {
    private List<TrainingTypeInfo> trainingTypeList;
}
