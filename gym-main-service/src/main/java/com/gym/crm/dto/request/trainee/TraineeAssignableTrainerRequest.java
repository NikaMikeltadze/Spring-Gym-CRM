package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TraineeAssignableTrainerRequest {
    @NotBlank(message = "Username should not be Null")
    private String username;
}
