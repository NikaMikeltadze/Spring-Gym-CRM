package com.gym.crm.dto.response.trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainerMonthlyWorkloadResponse {
    private String trainerUsername;
    private Integer year;
    private Integer month;
    private Double trainingSummaryDuration;
}
