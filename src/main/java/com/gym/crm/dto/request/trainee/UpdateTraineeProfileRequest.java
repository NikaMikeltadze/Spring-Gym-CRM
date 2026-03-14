package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTraineeProfileRequest {

    @NotBlank(message = "Username should not be Blank")
    private String username;

    @NotBlank(message = "First Name should not be Blank")
    private String firstName;

    @NotBlank(message = "Last Name should not be Blank")
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @NotNull(message = "isActive should not be Null")
    private Boolean isActive;


}
