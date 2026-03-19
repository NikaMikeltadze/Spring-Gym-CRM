package com.gym.crm.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTrainerProfileRequest {
    @NotBlank(message = "Username should not be Blank")
    private String username;

    @NotBlank(message = "First Name should not be Blank")
    private String firstName;

    @NotBlank(message = "Last Name should not be Blank")
    private String lastName;

    @NotNull(message = "isActive should not be Null")
    private Boolean isActive;
}
