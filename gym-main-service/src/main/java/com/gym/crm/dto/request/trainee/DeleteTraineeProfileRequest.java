package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteTraineeProfileRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;
}
