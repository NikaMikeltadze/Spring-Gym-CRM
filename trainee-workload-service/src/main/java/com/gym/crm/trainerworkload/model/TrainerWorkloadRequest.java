package com.gym.crm.trainerworkload.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;

@Data
@NoArgsConstructor
public class TrainerWorkloadRequest {
    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainerFirstName;

    @NotBlank
    private String trainerLastName;

    private boolean isActive;

    @NotNull
    private Date trainingDate;

    @NotNull
    @Positive
    private Double trainingDuration;

    @NotNull
    private ActionType actionType;
}
