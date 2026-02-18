package com.gym.crm.dao;

import com.gym.crm.model.Trainer;

public interface TrainerDao {
    public void save(Trainer trainer);

    public Trainer findByUsername(String username);

    public Trainer findById(Long id);

    public void update(Trainer trainer);

    boolean exists(String username);
}

