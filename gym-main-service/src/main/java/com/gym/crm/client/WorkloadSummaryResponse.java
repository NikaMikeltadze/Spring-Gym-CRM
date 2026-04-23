package com.gym.crm.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadSummaryResponse {
    private String trainerUsername;
    private Integer year;
    private Integer month;
    private Double trainingSummaryDuration;
}
