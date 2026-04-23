package com.gym.crm.dto.response.trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegisterTrainerResponse {
    private String username;
    private String password;
    private String token;
}
