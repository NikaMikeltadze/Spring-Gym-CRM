package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetTraineeProfileRequest {
    @NotBlank(message = "Username should not be Blank")
    String username;
}
