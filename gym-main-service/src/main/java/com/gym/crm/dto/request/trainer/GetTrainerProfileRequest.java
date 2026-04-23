package com.gym.crm.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetTrainerProfileRequest {
    @NotBlank(message = "Trainer Username should not be Blank")
    private String username;
}
