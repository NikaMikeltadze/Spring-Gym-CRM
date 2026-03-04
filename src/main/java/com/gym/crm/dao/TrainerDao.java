package com.gym.crm.dao;

import com.gym.crm.entity.Trainer;

public interface TrainerDao {
    void save(Trainer trainer);

    Trainer findByUsername(String username);

    Trainer findById(Long id);

    void update(Trainer trainer);

    boolean exists(String username);
}

