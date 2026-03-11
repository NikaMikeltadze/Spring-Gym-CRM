package com.gym.crm.dto.response.trainer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class GetTrainerTrainingListResponse {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingTypeName;
    private Double trainingDuration;
    private String traineeName;
}
