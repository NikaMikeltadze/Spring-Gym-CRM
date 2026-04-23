package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerDTO;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.TraineeProfileInfo;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.entity.*;
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
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setUsername(trainer.getUser().getUsername());
        dto.setIsActive(trainer.getUser().getIsActive());
        dto.setTrainingTypeId(trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null);
        dto.setTrainingTypeName(trainer.getTrainingType() != null ? trainer.getTrainingType().getName() : null);
        return dto;
    }

    public Trainer toEntity(TrainerDTO dto) {
        if (dto == null) return null;

        Trainer trainer = new Trainer();
        trainer.setId(dto.getId());
        trainer.getUser().setFirstName(dto.getFirstName());
        trainer.getUser().setLastName(dto.getLastName());
        trainer.getUser().setUsername(dto.getUsername());
        trainer.getUser().setIsActive(dto.getIsActive());
//        trainer.setTrainingType();
        return trainer;
    }

    public Trainer toEntity(RegisterTrainerRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        TrainingType trainingType = new TrainingType();

        trainingType.setId(request.getTrainingTypeId());
        trainer.setTrainingType(trainingType);

        return trainer;
    }

    public TrainerProfileInfo toProfileInfo(Trainer trainer) {
        if (trainer == null) return null;

        return new TrainerProfileInfo(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null
        );
    }

    public RegisterTrainerResponse toRegisterResponse(Trainer trainer) {
        if (trainer == null) return null;

        return new RegisterTrainerResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword(),
                null
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
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null,
                trainer.getUser().getIsActive(),
                traineeResponses
        );
    }

    public UpdateTrainerProfileResponse toUpdateProfileResponse(Trainer trainer) {
        if (trainer == null) return null;

        List<TraineeProfileInfo> traineeInfos = trainer.getTrainees() != null
                ? trainer.getTrainees().stream()
                  .map(trainee -> new TraineeProfileInfo(
                          trainee.getUser().getUsername(),
                          trainee.getUser().getFirstName(),
                          trainee.getUser().getLastName()
                  ))
                  .collect(Collectors.toList())
                : new ArrayList<>();

        return new UpdateTrainerProfileResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null,
                trainer.getUser().getIsActive(),
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
                training.getTrainee() != null ? training.getTrainee().getUser().getUsername() : null
        );
    }

    public void updateEntityFromRequest(UpdateTrainerProfileRequest request, Trainer trainer) {
        trainer.getUser().setFirstName(request.getFirstName());
        trainer.getUser().setLastName(request.getLastName());
        trainer.getUser().setIsActive(request.getIsActive());
    }

    private GetTraineeProfileResponse toNestedTraineeResponse(Trainee trainee) {
        if (trainee == null) return null;

        return GetTraineeProfileResponse.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().getIsActive())
                .trainers(null)
                .build();
    }
}
