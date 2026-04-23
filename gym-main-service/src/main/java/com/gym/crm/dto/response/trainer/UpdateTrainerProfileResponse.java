package com.gym.crm.dto.response.trainer;

import com.gym.crm.dto.response.trainee.TraineeProfileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UpdateTrainerProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Long trainingTypeId;
    private Boolean isActive;
    private List<TraineeProfileInfo> traineeList;
}
