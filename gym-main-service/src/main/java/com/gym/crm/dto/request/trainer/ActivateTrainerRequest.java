package com.gym.crm.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivateTrainerRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;

    @NotNull(message = "isActive should not be Null")
    private Boolean isActive;
}
