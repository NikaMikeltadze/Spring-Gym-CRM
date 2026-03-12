package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.dto.response.trainee.GetTraineeTrainingsResponse;
import com.gym.crm.entity.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TrainingDTO toDto(Training training) {
        if (training == null) return null;

        TrainingDTO dto = new TrainingDTO();
        dto.setId(training.getId());
        dto.setTraineeId(training.getTrainee() != null ? training.getTrainee().getId() : null);
        dto.setTraineeUsername(training.getTrainee() != null ? training.getTrainee().getUsername() : null);
        dto.setTrainerId(training.getTrainer() != null ? training.getTrainer().getId() : null);
        dto.setTrainerUsername(training.getTrainer() != null ? training.getTrainer().getUsername() : null);
        dto.setTrainingName(training.getTrainingName());
        dto.setTrainingTypeId(training.getTrainingType() != null ? training.getTrainingType().getId() : null);
        dto.setTrainingTypeName(training.getTrainingType() != null ? training.getTrainingType().getName() : null);
        dto.setTrainingDate(training.getTrainingDate());
        dto.setTrainingDuration(training.getTrainingDuration());
        return dto;
    }

    public Training toEntity(TrainingDTO dto) {
        if (dto == null) return null;

        Training training = Training.builder()
                .id(dto.getId())
                .trainingName(dto.getTrainingName())
                .trainingDate(dto.getTrainingDate())
                .trainingDuration(dto.getTrainingDuration())
//                .trainee()
//                .trainer()
//                .trainingType()
                .build();
        return training;
    }

    public GetTraineeTrainingsResponse toGetTraineeTrainingsResponse(Training training) {
        if (training == null) return null;

        return GetTraineeTrainingsResponse.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingTypeName(training.getTrainingType().getName())
                .trainingDuration(training.getTrainingDuration())
                .trainerName(training.getTrainer().getFirstName())
                .build();
    }
}
