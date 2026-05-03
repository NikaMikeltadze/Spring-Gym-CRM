package com.gym.crm.trainerworkload.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearSummary {
    private int year;
    private List<MonthSummary> months = new ArrayList<>();
}
