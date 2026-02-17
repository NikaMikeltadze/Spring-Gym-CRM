package com.gym.crm.dao;

import com.gym.crm.model.TrainingType;

import java.util.List;

public interface TrainingTypeDao {
    public void save(TrainingType trainingType);

    public TrainingType findById(Long id);

    List<TrainingType> findAll();
}
