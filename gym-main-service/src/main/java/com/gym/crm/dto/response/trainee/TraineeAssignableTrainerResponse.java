package com.gym.crm.dto.response.trainee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TraineeAssignableTrainerResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Long trainingTypeId;
}
