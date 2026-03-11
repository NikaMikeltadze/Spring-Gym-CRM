package com.gym.crm.dto.response.trainer;

import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetTrainerProfileResponse {
    private String firstName;
    private String lastName;
    private Long TrainingTypeId;
    private Boolean isActive;
    private List<GetTraineeProfileResponse> traineeList;
}
