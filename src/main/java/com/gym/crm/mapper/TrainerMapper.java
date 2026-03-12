package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerDTO;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.TraineeProfileInfo;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerMapper {
    public TrainerDTO toDto(Trainer trainer) {
        if (trainer == null) return null;

        TrainerDTO dto = new TrainerDTO();
        dto.setId(trainer.getId());
        dto.setFirstName(trainer.getFirstName());
        dto.setLastName(trainer.getLastName());
        dto.setUsername(trainer.getUsername());
        dto.setIsActive(trainer.getIsActive());
        dto.setTrainingTypeId(trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null);
        dto.setTrainingTypeName(trainer.getTrainingType() != null ? trainer.getTrainingType().getName() : null);
        return dto;
    }

    public Trainer toEntity(TrainerDTO dto) {
        if (dto == null) return null;

        Trainer trainer = new Trainer();
        trainer.setId(dto.getId());
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainer.setUsername(dto.getUsername());
        trainer.setIsActive(dto.getIsActive());
//        trainer.setTrainingType();
        return trainer;
    }

    public Trainer toEntity(RegisterTrainerRequest request) {
        if (request == null) return null;

        Trainer trainer = new Trainer();
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
//        trainer.setTrainingType();
        return trainer;
    }

    public TrainerProfileInfo toProfileInfo(Trainer trainer) {
        if (trainer == null) return null;

        return new TrainerProfileInfo(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null
        );
    }

    public RegisterTrainerResponse toRegisterResponse(Trainer trainer) {
        if (trainer == null) return null;

        return new RegisterTrainerResponse(
                trainer.getUsername(),
                trainer.getPassword()
        );
    }

    public GetTrainerProfileResponse toGetProfileResponse(Trainer trainer) {
        if (trainer == null) return null;

        List<GetTraineeProfileResponse> traineeResponses = trainer.getTrainees() != null
                ? trainer.getTrainees().stream()
                .map(this::toNestedTraineeResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new GetTrainerProfileResponse(
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null,
                trainer.getIsActive(),
                traineeResponses
        );
    }

    public UpdateTrainerProfileResponse toUpdateProfileResponse(Trainer trainer) {
        if (trainer == null) return null;

        List<TraineeProfileInfo> traineeInfos = trainer.getTrainees() != null
                ? trainer.getTrainees().stream()
                .map(trainee -> new TraineeProfileInfo(
                        trainee.getUsername(),
                        trainee.getFirstName(),
                        trainee.getLastName()
                ))
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new UpdateTrainerProfileResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null,
                trainer.getIsActive(),
                traineeInfos
        );
    }

    public GetTrainerTrainingsResponse toGetTrainingsResponse(Training training) {
        if (training == null) return null;

        return new GetTrainerTrainingsResponse(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType() != null ? training.getTrainingType().getName() : null,
                training.getTrainingDuration(),
                training.getTrainee() != null ? training.getTrainee().getUsername() : null
        );
    }

    private GetTraineeProfileResponse toNestedTraineeResponse(Trainee trainee) {
        if (trainee == null) return null;

        return GetTraineeProfileResponse.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getIsActive())
                .trainers(null)
                .build();
    }
}
