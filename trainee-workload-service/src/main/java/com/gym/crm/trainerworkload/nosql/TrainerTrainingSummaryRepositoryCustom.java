package com.gym.crm.trainerworkload.nosql;

public interface TrainerTrainingSummaryRepositoryCustom {
     // Atomically increment the trainingSummaryDuration for a specific year/month.
    boolean incrementMonthlyDuration(double delta, int year, int month);
}
