package com.gym.crm.trainerworkload.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {
    @Min(1)
    private int month;

    @Min(0)
    private double trainingSummaryDuration;
}
