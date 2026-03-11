package com.gym.crm.dto.response.trainee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterTraineeResponse {
    private String username;
    private String password;
}
