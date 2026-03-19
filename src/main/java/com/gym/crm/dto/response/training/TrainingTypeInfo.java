package com.gym.crm.dto.response.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TrainingTypeInfo {
    private Long trainingTypeId;
    private String trainingTypeName;
}
