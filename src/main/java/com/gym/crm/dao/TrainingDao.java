package com.gym.crm.dao;

import com.gym.crm.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingDao {
    void save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findByTraineeUsername(String traineeUsername);

    List<Training> findByTrainerUsername(String trainerUsername);

    List<Training> findByDateRange(LocalDate fromDate, LocalDate toDate);

    List<Training> findByTraineeId(Long traineeId);

    List<Training> findByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    );

    List<Training> findByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );
}

