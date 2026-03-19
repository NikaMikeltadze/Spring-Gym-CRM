package com.gym.crm.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTraineeTrainerListRequest {
    @NotBlank(message = "Trainee Username should not be Null")
    private String traineeUsername;

    @NotNull(message = "Trainer Username List should not be Null")
    List<String> trainerUsernameList;
}
