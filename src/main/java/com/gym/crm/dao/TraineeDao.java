package com.gym.crm.dao;

import com.gym.crm.model.Trainee;

public interface TraineeDao {
    public void save(Trainee trainee);

    public Trainee findById(Long id);

    public Trainee findByUsername(String username);

    public void delete(String username);

    public void update(Trainee trainee);

    boolean exists(String username);
}
