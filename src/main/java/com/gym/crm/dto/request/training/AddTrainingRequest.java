package com.gym.crm.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddTrainingRequest {
    @NotBlank(message = "Trainee Username should not be Null")
    private String TraineeUsername;

    @NotBlank(message = "Trainer Username should not be Null")
    private String TrainerUsername;

    @NotBlank(message = "Training Name should not be Null")
    private String TrainingName;

    @NotNull(message = "Training Date should not be Null")
    @PastOrPresent(message = "Training Date should be in the past or present")
    private LocalDate TrainingDate;

    @NotNull(message = "Training Duration should not be Null")
    private Double TrainingDuration;

}
