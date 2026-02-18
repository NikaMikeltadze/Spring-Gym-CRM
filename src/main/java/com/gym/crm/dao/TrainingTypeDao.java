package com.gym.crm.dao;

import com.gym.crm.model.TrainingType;

import java.util.List;

public interface TrainingTypeDao {
    void save(TrainingType trainingType);

    TrainingType findById(Long id);

    List<TrainingType> findAll();
}

