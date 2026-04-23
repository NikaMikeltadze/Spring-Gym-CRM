package com.gym.crm.trainerworkload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadMonthSummary {
    private int month;
    private double trainingSummaryDuration;
}
