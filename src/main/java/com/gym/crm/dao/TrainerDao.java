package com.gym.crm.dao;

import com.gym.crm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    void save(Trainer trainer);

    Optional<Trainer> findByUsername(String username);

    Optional<Trainer> findById(Long id);

    void update(Trainer trainer);

    boolean exists(String username);

    List<Trainer> findActiveTrainersNotAssignedTo(String traineeUsername);
}
