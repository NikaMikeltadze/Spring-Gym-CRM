package com.gym.crm.dao;

import com.gym.crm.model.Trainee;

public interface TraineeDao {
    void save(Trainee trainee);

    Trainee findById(Long id);

    Trainee findByUsername(String username);

    void delete(String username);

    void update(Trainee trainee);

    boolean exists(String username);
}

