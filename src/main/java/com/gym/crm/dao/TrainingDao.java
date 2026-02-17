package com.gym.crm.dao;

import com.gym.crm.model.Training;

import java.util.List;

public interface TrainingDao {
    public void save(Training training);

    public Training findById(Long id);

    List<Training> findAll();
}
