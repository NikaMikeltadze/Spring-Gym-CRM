package com.gym.crm.trainerworkload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMonthlyWorkloadResponse {
	private String trainerUsername;
	private Integer year;
	private Integer month;
	private Double trainingSummaryDuration;
}
