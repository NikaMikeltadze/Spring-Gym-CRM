package com.gym.crm.trainerworkload.nosql;

import com.gym.crm.trainerworkload.model.ActionType;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class TrainerTrainingSummaryService {

    private final TrainerTrainingSummaryRepository repository;

    /**
     * Process incoming training event: upsert or update a trainer's month summary.
     * Creates new document if trainer not found, adds new year/month as needed,
     * and updates trainingSummaryDuration by delta (ADD increases, DELETE decreases)
     */
    @Transactional
    public void processEvent(@Valid TrainerWorkloadRequest request) {
        String transactionId = MDC.get("transactionId");
        log.info("tx={} - START processing training event: username={} firstName={} lastName={} actionType={} trainingDuration={}",
                transactionId, request.getTrainerUsername(), request.getTrainerFirstName(),
                request.getTrainerLastName(), request.getActionType(), request.getTrainingDuration());

        try {
            validateRequest(request, transactionId);
            LocalDate localDate = request.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();
            log.debug("tx={} - Extracted date: year={} month={}", transactionId, year, month);

            double delta = request.getActionType() == ActionType.ADD
                    ? request.getTrainingDuration()
                    : -request.getTrainingDuration();
            log.debug("tx={} - Calculated delta={}", transactionId, delta);

            try {
                if (repository.incrementMonthlyDuration(delta, year, month)) {
                    log.info("tx={} - COMPLETE: atomic update applied for {}/{}/{}", transactionId,
                            request.getTrainerUsername(), year, month);
                    return;
                }
            } catch (Exception e) {
                log.debug("tx={} - Atomic update failed, falling back to read-modify-write: {}", transactionId, e.getMessage());
            }

            // Fallback: read-modify-write approach
            Optional<TrainerTrainingSummary> existing = repository.findByUsername(request.getTrainerUsername());

            if (existing.isEmpty()) {
                createNewTrainerDocument(request, year, month, delta, transactionId);
            } else {
                updateExistingTrainerDocument(existing.get(), request, year, month, delta, transactionId);
            }

            log.info("tx={} - COMPLETE: training event processed successfully", transactionId);

        } catch (IllegalArgumentException e) {
            log.error("tx={} - Validation error: {}", transactionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("tx={} - Unexpected error processing training event", transactionId, e);
            throw e;
        }
    }

    private void validateRequest(TrainerWorkloadRequest request, String transactionId) {
        if (request.getTrainerUsername() == null || request.getTrainerUsername().isBlank()) {
            throw new IllegalArgumentException("trainerUsername must not be blank");
        }
        if (request.getTrainerFirstName() == null || request.getTrainerFirstName().isBlank()) {
            throw new IllegalArgumentException("trainerFirstName must not be blank");
        }
        if (request.getTrainerLastName() == null || request.getTrainerLastName().isBlank()) {
            throw new IllegalArgumentException("trainerLastName must not be blank");
        }
        if (request.getTrainingDate() == null) {
            throw new IllegalArgumentException("trainingDate must not be null");
        }
        if (request.getTrainingDuration() == null || request.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("trainingDuration must be positive");
        }
        if (request.getActionType() == null) {
            throw new IllegalArgumentException("actionType must not be null");
        }
        log.debug("tx={} - Request validation passed", transactionId);
    }

    private void createNewTrainerDocument(TrainerWorkloadRequest request, int year, int month,
                                         double delta, String transactionId) {
        TrainerTrainingSummary newDoc = new TrainerTrainingSummary();
        newDoc.setUsername(request.getTrainerUsername());
        newDoc.setFirstName(request.getTrainerFirstName());
        newDoc.setLastName(request.getTrainerLastName());
        newDoc.setStatus(request.isActive());

        YearSummary yearSummary = new YearSummary();
        yearSummary.setYear(year);
        yearSummary.setMonths(new ArrayList<>());

        double initialDuration = Math.max(0.0, delta);
        MonthSummary monthSummary = new MonthSummary(month, initialDuration);
        yearSummary.getMonths().add(monthSummary);

        newDoc.setYears(new ArrayList<>());
        newDoc.getYears().add(yearSummary);

        repository.save(newDoc);
        log.info("tx={} - Created new TrainerTrainingSummary for username={} with {}/{} month duration={}",
                transactionId, request.getTrainerUsername(), year, month, initialDuration);
    }

    private void updateExistingTrainerDocument(TrainerTrainingSummary doc, TrainerWorkloadRequest request,
                                              int year, int month, double delta, String transactionId) {
        doc.setStatus(request.isActive());
        log.debug("tx={} - Updated trainer status to {}", transactionId, request.isActive());

        YearSummary targetYear = doc.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElse(null);

        if (targetYear == null) {
            targetYear = new YearSummary(year, new ArrayList<>());
            doc.getYears().add(targetYear);
            log.debug("tx={} - Added new year: {}", transactionId, year);
        }

        MonthSummary targetMonth = targetYear.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElse(null);

        if (targetMonth == null) {
            double initialDuration = Math.max(0.0, delta);
            targetMonth = new MonthSummary(month, initialDuration);
            targetYear.getMonths().add(targetMonth);
            log.debug("tx={} - Added new month: {} with duration={}", transactionId, month, initialDuration);
        } else {
            double oldDuration = targetMonth.getTrainingSummaryDuration();
            double newDuration = Math.max(0.0, oldDuration + delta);
            targetMonth.setTrainingSummaryDuration(newDuration);
            log.debug("tx={} - Updated month {} duration: {} + {} = {}", transactionId, month, oldDuration, delta, newDuration);
        }

        repository.save(doc);
        log.debug("tx={} - Saved updated document for username={}", transactionId, request.getTrainerUsername());
    }
}
