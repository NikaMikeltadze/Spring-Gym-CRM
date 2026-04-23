package com.gym.crm.trainerworkload.repository;

import com.gym.crm.trainerworkload.model.WorkloadMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkloadMonthRepository extends JpaRepository<WorkloadMonth, Long> {
    Optional<WorkloadMonth> findByTrainerUsernameAndYearAndMonth(String trainerUsername, int year, int month);

    List<WorkloadMonth> findAllByTrainerUsernameOrderByYearAscMonthAsc(String trainerUsername);
}
