package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetTraineeTrainingListRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;

    private LocalDate startDate;

    private LocalDate endDate;

    private String trainerName;

    private String trainingTypeName;
}
