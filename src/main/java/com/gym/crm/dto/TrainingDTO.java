package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {

    private Long id;

    @NotNull(message = "Trainee ID must not be null")
    private Long traineeId;

    private String traineeUsername;

    @NotNull(message = "Trainer ID must not be null")
    private Long trainerId;

    private String trainerUsername;

    @NotBlank(message = "Training name must not be blank")
    private String trainingName;

    @NotNull(message = "Training type ID must not be null")
    private Long trainingTypeId;

    private String trainingTypeName;

    @NotNull(message = "Training date must not be null")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration must not be null")
    @Positive(message = "Training duration must be positive")
    private Double trainingDuration;
}
