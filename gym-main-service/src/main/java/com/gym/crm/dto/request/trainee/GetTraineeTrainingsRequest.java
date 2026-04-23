package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetTraineeTrainingsRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;

    private LocalDate startDate;

    private LocalDate endDate;

    private String trainerName;

    private String trainingTypeName;
}
