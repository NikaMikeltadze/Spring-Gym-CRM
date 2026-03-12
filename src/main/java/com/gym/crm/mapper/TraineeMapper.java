package com.gym.crm.mapper;

import com.gym.crm.dto.TraineeDTO;
import com.gym.crm.dto.request.trainee.RegisterTraineeRequest;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.GetTraineeTrainingsResponse;
import com.gym.crm.dto.response.trainee.RegisterTraineeResponse;
import com.gym.crm.dto.response.trainee.TraineeAssignableTrainerResponse;
import com.gym.crm.dto.response.trainee.TraineeProfileInfo;
import com.gym.crm.dto.response.trainee.UpdateTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeTrainerListResponse;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TraineeMapper {
    public TraineeDTO toDto(Trainee trainee) {
        if (trainee == null) return null;

        TraineeDTO dto = new TraineeDTO();
        dto.setId(trainee.getId());
        dto.setFirstName(trainee.getFirstName());
        dto.setLastName(trainee.getLastName());
        dto.setUsername(trainee.getUsername());
        dto.setIsActive(trainee.getIsActive());
        dto.setDateOfBirth(trainee.getDateOfBirth());
        dto.setAddress(trainee.getAddress());
        return dto;
    }

    public Trainee toEntity(TraineeDTO dto) {
        if (dto == null) return null;

        Trainee trainee = new Trainee();
        trainee.setId(dto.getId());
        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setUsername(dto.getUsername());
        trainee.setIsActive(dto.getIsActive());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
        return trainee;
    }

    public Trainee toEntity(RegisterTraineeRequest request) {
        if (request == null) return null;

        Trainee trainee = new Trainee();
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        return trainee;
    }

    public TraineeProfileInfo toProfileInfo(Trainee trainee) {
        if (trainee == null) return null;

        return new TraineeProfileInfo(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName()
        );
    }

    public RegisterTraineeResponse toRegisterResponse(Trainee trainee) {
        if (trainee == null) return null;

        return new RegisterTraineeResponse(
                trainee.getUsername(),
                trainee.getPassword()
        );
    }

    public GetTraineeProfileResponse toGetProfileResponse(Trainee trainee) {
        if (trainee == null) return null;

        List<GetTrainerProfileResponse> trainerResponses = trainee.getTrainers() != null
                ? trainee.getTrainers().stream()
                .map(this::toNestedTrainerResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return GetTraineeProfileResponse.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getIsActive())
                .trainers(trainerResponses)
                .build();
    }

    public UpdateTraineeProfileResponse toUpdateProfileResponse(Trainee trainee) {
        if (trainee == null) return null;

        List<GetTrainerProfileResponse> trainerResponses = trainee.getTrainers() != null
                ? trainee.getTrainers().stream()
                .map(this::toNestedTrainerResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return UpdateTraineeProfileResponse.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getIsActive())
                .trainers(trainerResponses)
                .build();
    }

    public UpdateTraineeTrainerListResponse toUpdateTrainerListResponse(List<Trainer> trainers) {
        List<TrainerProfileInfo> trainerInfos = trainers != null
                ? trainers.stream()
                .map(trainer -> new TrainerProfileInfo(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null
                ))
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new UpdateTraineeTrainerListResponse(trainerInfos);
    }

    public TraineeAssignableTrainerResponse toAssignableTrainerResponse(Trainer trainer) {
        if (trainer == null) return null;

        return new TraineeAssignableTrainerResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null
        );
    }

    public GetTraineeTrainingsResponse toGetTrainingsResponse(Training training) {
        if (training == null) return null;

        return new GetTraineeTrainingsResponse(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType() != null ? training.getTrainingType().getName() : null,
                training.getTrainingDuration(),
                training.getTrainer() != null ? training.getTrainer().getUsername() : null
        );
    }


    private GetTrainerProfileResponse toNestedTrainerResponse(Trainer trainer) {
        if (trainer == null) return null;

        return new GetTrainerProfileResponse(
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getTrainingType() != null ? trainer.getTrainingType().getId() : null,
                trainer.getIsActive(),
                null // for avoidding infinite recursion
        );
    }
}
