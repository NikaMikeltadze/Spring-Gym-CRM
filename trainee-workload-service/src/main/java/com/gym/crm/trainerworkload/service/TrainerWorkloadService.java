package com.gym.crm.trainerworkload.service;

import com.gym.crm.trainerworkload.model.ActionType;
import com.gym.crm.trainerworkload.model.TrainerMonthlyWorkloadResponse;
import com.gym.crm.trainerworkload.model.TrainerWorkload;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import com.gym.crm.trainerworkload.model.TrainerWorkloadSummaryResponse;
import com.gym.crm.trainerworkload.model.WorkloadMonth;
import com.gym.crm.trainerworkload.model.WorkloadMonthSummary;
import com.gym.crm.trainerworkload.model.WorkloadYearSummary;
import com.gym.crm.trainerworkload.repository.TrainerWorkloadRepository;
import com.gym.crm.trainerworkload.repository.WorkloadMonthRepository;
import com.gym.crm.trainerworkload.nosql.TrainerTrainingSummaryService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final WorkloadMonthRepository workloadMonthRepository;
    private final TrainerTrainingSummaryService trainerTrainingSummaryService;

    public TrainerWorkloadService(TrainerWorkloadRepository trainerWorkloadRepository, WorkloadMonthRepository workloadMonthRepository, TrainerTrainingSummaryService trainerTrainingSummaryService) {
        this.trainerWorkloadRepository = trainerWorkloadRepository;
        this.workloadMonthRepository = workloadMonthRepository;
        this.trainerTrainingSummaryService = trainerTrainingSummaryService;
    }

    @Transactional
    public void processWorkload(TrainerWorkloadRequest request) {
        if (request.getTrainingDate() == null) {
            throw new IllegalArgumentException("trainingDate must not be null");
        }
        if (request.getActionType() == null) {
            throw new IllegalArgumentException("actionType must not be null");
        }

        Double requestDurationValue = request.getTrainingDuration();
        if (requestDurationValue == null) {
            throw new IllegalArgumentException("trainingDuration must not be null");
        }
        if (requestDurationValue <= 0) {
            throw new IllegalArgumentException("trainingDuration must be greater than zero");
        }
        double requestedDuration = requestDurationValue;

        TrainerWorkload trainer = trainerWorkloadRepository.findByUsername(request.getTrainerUsername())
                .orElseGet(() -> {
                    TrainerWorkload newTrainer = new TrainerWorkload();
                    newTrainer.setUsername(request.getTrainerUsername());
                    newTrainer.setFirstName(request.getTrainerFirstName());
                    newTrainer.setLastName(request.getTrainerLastName());
                    return newTrainer;
                });
        trainer.setActive(request.isActive());
        trainerWorkloadRepository.save(trainer);

        LocalDate localDate = request.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();

        WorkloadMonth workloadMonth = workloadMonthRepository.findByTrainerUsernameAndYearAndMonth(request.getTrainerUsername(), year, month)
                .orElseGet(() -> {
                    WorkloadMonth newWorkloadMonth = new WorkloadMonth();
                    newWorkloadMonth.setTrainerUsername(request.getTrainerUsername());
                    newWorkloadMonth.setYear(year);
                    newWorkloadMonth.setMonth(month);
                    newWorkloadMonth.setTrainingDuration(0.0);
                    return newWorkloadMonth;
                });

        double currentDuration = workloadMonth.getTrainingDuration() == null ? 0.0 : workloadMonth.getTrainingDuration();

        if (request.getActionType() == ActionType.ADD) {
            workloadMonth.setTrainingDuration(currentDuration + requestedDuration);
        } else if (request.getActionType() == ActionType.DELETE) {
            double newDuration = currentDuration - requestedDuration;
            workloadMonth.setTrainingDuration(Math.max(0.0, newDuration));
        }

        workloadMonthRepository.save(workloadMonth);
        trainerTrainingSummaryService.processEvent(request);
    }

    @Transactional
    public TrainerWorkloadSummaryResponse getTrainerWorkloadSummary(String trainerUsername, Integer year, Integer month) {
        TrainerWorkload trainer = trainerWorkloadRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerUsername));

        List<WorkloadMonth> months = workloadMonthRepository.findAllByTrainerUsernameOrderByYearAscMonthAsc(trainerUsername);
        Map<Integer, List<WorkloadMonthSummary>> groupedByYear = new TreeMap<>();

        if (year != null) {
            if (year < 1) {
                throw new IllegalArgumentException("year must be positive");
            }
            groupedByYear.put(year, new ArrayList<>());
        }
        if (month != null) {
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("month must be between 1 and 12");
            }
        }

        for (WorkloadMonth workloadMonth : months) {
            if (year != null && workloadMonth.getYear() != year) {
                continue;
            }
            if (month != null && workloadMonth.getMonth() != month) {
                continue;
            }

            groupedByYear
                    .computeIfAbsent(workloadMonth.getYear(), ignored -> new ArrayList<>())
                    .add(new WorkloadMonthSummary(
                            workloadMonth.getMonth(),
                            workloadMonth.getTrainingDuration() == null ? 0.0 : workloadMonth.getTrainingDuration()
                    ));
        }

        if (year != null && month != null && groupedByYear.get(year).isEmpty()) {
            // Return explicit zero duration for requested period even if no sessions exist.
            groupedByYear.get(year).add(new WorkloadMonthSummary(month, 0.0));
        }

        List<WorkloadYearSummary> years = new ArrayList<>();
        for (Map.Entry<Integer, List<WorkloadMonthSummary>> entry : groupedByYear.entrySet()) {
            years.add(new WorkloadYearSummary(entry.getKey(), entry.getValue()));
        }

        return new TrainerWorkloadSummaryResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                years
        );
    }

    @Transactional
    public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(String trainerUsername, Integer year, Integer month) {
        if (year == null || year < 1) {
            throw new IllegalArgumentException("year must be positive");
        }
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be between 1 and 12");
        }

        trainerWorkloadRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerUsername));

        WorkloadMonth monthly = workloadMonthRepository
                .findByTrainerUsernameAndYearAndMonth(trainerUsername, year, month)
                .orElse(null);

        double duration = monthly == null || monthly.getTrainingDuration() == null
                ? 0.0
                : monthly.getTrainingDuration();

        return new TrainerMonthlyWorkloadResponse(trainerUsername, year, month, duration);
    }
}
