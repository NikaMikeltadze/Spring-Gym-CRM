package com.gym.crm.dto.response.trainer;

import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetTrainerProfileResponse {
    private String firstName;
    private String lastName;
    private Long TrainingTypeId;
    private Boolean isActive;
    private List<GetTraineeProfileResponse> traineeList;
}
