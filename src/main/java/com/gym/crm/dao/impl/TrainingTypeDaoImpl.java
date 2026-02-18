package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.model.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private final Map<Long, TrainingType> trainingTypeStorage;

    public TrainingTypeDaoImpl(Map<Long, TrainingType> trainingTypeStorage) {
        this.trainingTypeStorage = trainingTypeStorage;
    }

    @Override
    public void save(TrainingType trainingType) {
        if (trainingType.getId() == null) trainingType.setId(trainingTypeStorage.size() + 1L);
        trainingTypeStorage.put(trainingType.getId(), trainingType);
    }

    @Override
    public TrainingType findById(Long id) {
        return trainingTypeStorage.get(id);
    }

    @Override
    public List<TrainingType> findAll() {
        return trainingTypeStorage.values().stream().toList();
    }
}

