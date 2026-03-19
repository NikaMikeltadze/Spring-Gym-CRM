package com.gym.crm.dao;

import com.gym.crm.entity.Trainee;

import java.util.Optional;

public interface TraineeDao {
    void save(Trainee trainee);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    void delete(String username);

    void update(Trainee trainee);

    boolean exists(String username);
}
