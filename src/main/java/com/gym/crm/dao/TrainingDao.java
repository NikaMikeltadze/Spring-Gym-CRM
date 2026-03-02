package com.gym.crm.dao;

import com.gym.crm.entity.Training;

import java.util.List;

public interface TrainingDao {
    void save(Training training);

    Training findById(Long id);

    List<Training> findAll();
}

