package com.gym.crm.dto.response.trainee;

import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UpdateTraineeTrainerListResponse {
    private List<TrainerProfileInfo> trainerList;
}
