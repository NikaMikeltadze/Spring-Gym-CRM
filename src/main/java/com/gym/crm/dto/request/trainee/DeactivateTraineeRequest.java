package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DeactivateTraineeRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;

    @NotNull(message = "isActive should not be Null")
    private Boolean isActive;
}
