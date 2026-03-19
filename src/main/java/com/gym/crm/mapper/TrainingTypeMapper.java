package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingTypeDTO;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.TrainingType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingTypeMapper {

    public TrainingTypeDTO toDto(TrainingType trainingType) {
        if (trainingType == null) return null;

        TrainingTypeDTO dto = new TrainingTypeDTO();
        dto.setId(trainingType.getId());
        dto.setName(trainingType.getName());
        return dto;
    }

    public TrainingType toEntity(TrainingTypeDTO dto) {
        if (dto == null) return null;

        TrainingType trainingType = new TrainingType();
        trainingType.setId(dto.getId());
        trainingType.setName(dto.getName());
        return trainingType;
    }

    public TrainingTypeInfo toTrainingTypeInfo(TrainingType trainingType) {
        if (trainingType == null) return null;

        return new TrainingTypeInfo(
                trainingType.getId(),
                trainingType.getName()
        );
    }

    public GetTrainingTypesResponse toGetTrainingTypesResponse(List<TrainingType> trainingTypes) {
        List<TrainingTypeInfo> infoList = trainingTypes != null
                ? trainingTypes.stream()
                .map(this::toTrainingTypeInfo)
                .collect(Collectors.toList())
                : List.of();

        return new GetTrainingTypesResponse(infoList);
    }
}
