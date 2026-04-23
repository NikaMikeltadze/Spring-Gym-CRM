package com.gym.crm.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterTrainerRequest {
    @NotBlank(message = "First name must not be blank")
    String firstName;

    @NotBlank(message = "Last name must not be blank")
    String lastName;

    @NotNull(message = "Training type ID must not be null")
    Long trainingTypeId;
}
