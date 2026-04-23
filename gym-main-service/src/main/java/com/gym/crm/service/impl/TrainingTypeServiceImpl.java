package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.mapper.TrainingTypeMapper;
import com.gym.crm.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public Optional<TrainingTypeInfo> getById(Long id) {
        TrainingType trainingType = trainingTypeDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Training type not found with id: " + id));
        TrainingTypeInfo response = trainingTypeMapper.toTrainingTypeInfo(trainingType);
        return Optional.ofNullable(response);
    }

    @Override
    public Optional<GetTrainingTypesResponse> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeDao.findAll();
        List<TrainingTypeInfo> list = trainingTypes.stream().map(trainingTypeMapper::toTrainingTypeInfo).toList();
        GetTrainingTypesResponse response = new GetTrainingTypesResponse(list);
        return Optional.of(response);
    }
}
