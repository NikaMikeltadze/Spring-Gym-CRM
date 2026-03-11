package com.gym.crm.dto.response.trainer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrainerProfileInfo {
    private String username;
    private String firstName;
    private String lastName;
    private Long trainingTypeId;
}
