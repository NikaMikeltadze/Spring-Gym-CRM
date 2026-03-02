package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTypeDTO {

    private Long id;

    @NotBlank(message = "Training type name must not be blank")
    private String name;
}
