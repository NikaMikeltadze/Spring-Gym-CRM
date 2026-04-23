package com.gym.crm.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetTrainerTrainingsRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;

    private LocalDate startDate;
    private LocalDate endDate;

    private String traineeName;
}
