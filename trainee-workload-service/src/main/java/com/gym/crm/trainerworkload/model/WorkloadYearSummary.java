package com.gym.crm.trainerworkload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadYearSummary {
    private int year;
    private List<WorkloadMonthSummary> months;
}
