package com.gym.crm.dao;

import com.gym.crm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao {
    void save(TrainingType trainingType);

    Optional<TrainingType> findById(Long id);

    List<TrainingType> findAll();
}

