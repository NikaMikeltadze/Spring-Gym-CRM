package com.gym.crm.trainerworkload.service;

import com.gym.crm.trainerworkload.model.ActionType;
import com.gym.crm.trainerworkload.model.TrainerMonthlyWorkloadResponse;
import com.gym.crm.trainerworkload.model.TrainerWorkload;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import com.gym.crm.trainerworkload.model.TrainerWorkloadSummaryResponse;
import com.gym.crm.trainerworkload.model.WorkloadMonth;
import com.gym.crm.trainerworkload.model.WorkloadMonthSummary;
import com.gym.crm.trainerworkload.repository.TrainerWorkloadRepository;
import com.gym.crm.trainerworkload.repository.WorkloadMonthRepository;
import com.gym.crm.trainerworkload.nosql.TrainerTrainingSummaryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private WorkloadMonthRepository workloadMonthRepository;

    @Mock
    private TrainerTrainingSummaryService trainerTrainingSummaryService;

    @InjectMocks
    private TrainerWorkloadService trainerWorkloadService;

    private TimeZone originalTimeZone;

    @BeforeEach
    void setUpTimeZone() {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @AfterEach
    void restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    void processWorkload_createsTrainerAndMonthWhenMissing_addsDuration() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15T12:00:00Z", 2.5);

        when(trainerWorkloadRepository.findByUsername("jdoe")).thenReturn(Optional.empty());
        when(workloadMonthRepository.findByTrainerUsernameAndYearAndMonth("jdoe", 2024, 6)).thenReturn(Optional.empty());

        trainerWorkloadService.processWorkload(request);

        ArgumentCaptor<TrainerWorkload> trainerCaptor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(trainerCaptor.capture());
        assertThat(trainerCaptor.getValue().getUsername()).isEqualTo("jdoe");
        assertThat(trainerCaptor.getValue().getFirstName()).isEqualTo("John");
        assertThat(trainerCaptor.getValue().getLastName()).isEqualTo("Doe");
        assertThat(trainerCaptor.getValue().isActive()).isTrue();

        ArgumentCaptor<WorkloadMonth> monthCaptor = ArgumentCaptor.forClass(WorkloadMonth.class);
        verify(workloadMonthRepository).save(monthCaptor.capture());
        assertThat(monthCaptor.getValue().getTrainerUsername()).isEqualTo("jdoe");
        assertThat(monthCaptor.getValue().getYear()).isEqualTo(2024);
        assertThat(monthCaptor.getValue().getMonth()).isEqualTo(6);
        assertThat(monthCaptor.getValue().getTrainingDuration()).isEqualTo(2.5);
    }

    @Test
    void processWorkload_addsDurationToExistingMonth() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", false, ActionType.ADD, "2024-06-15T12:00:00Z", 1.75);

        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setUsername("jdoe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        WorkloadMonth month = new WorkloadMonth();
        month.setTrainerUsername("jdoe");
        month.setYear(2024);
        month.setMonth(6);
        month.setTrainingDuration(3.0);

        when(trainerWorkloadRepository.findByUsername("jdoe")).thenReturn(Optional.of(trainer));
        when(workloadMonthRepository.findByTrainerUsernameAndYearAndMonth("jdoe", 2024, 6)).thenReturn(Optional.of(month));

        trainerWorkloadService.processWorkload(request);

        ArgumentCaptor<TrainerWorkload> trainerCaptor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(trainerCaptor.capture());
        assertThat(trainerCaptor.getValue().isActive()).isFalse();

        ArgumentCaptor<WorkloadMonth> monthCaptor = ArgumentCaptor.forClass(WorkloadMonth.class);
        verify(workloadMonthRepository).save(monthCaptor.capture());
        assertThat(monthCaptor.getValue().getTrainingDuration()).isEqualTo(4.75);
    }

    @Test
    void processWorkload_deleteClampsDurationAtZero() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.DELETE, "2024-06-15T12:00:00Z", 5.0);

        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setUsername("jdoe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        WorkloadMonth month = new WorkloadMonth();
        month.setTrainerUsername("jdoe");
        month.setYear(2024);
        month.setMonth(6);
        month.setTrainingDuration(1.0);

        when(trainerWorkloadRepository.findByUsername("jdoe")).thenReturn(Optional.of(trainer));
        when(workloadMonthRepository.findByTrainerUsernameAndYearAndMonth("jdoe", 2024, 6)).thenReturn(Optional.of(month));

        trainerWorkloadService.processWorkload(request);

        ArgumentCaptor<WorkloadMonth> monthCaptor = ArgumentCaptor.forClass(WorkloadMonth.class);
        verify(workloadMonthRepository).save(monthCaptor.capture());
        assertThat(monthCaptor.getValue().getTrainingDuration()).isEqualTo(0.0);
    }

    @Test
    void processWorkload_rejectsNullTrainingDate() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15T12:00:00Z", 2.5);
        request.setTrainingDate(null);

        assertThatThrownBy(() -> trainerWorkloadService.processWorkload(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("trainingDate must not be null");
    }

    @Test
    void processWorkload_rejectsNonPositiveDuration() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15T12:00:00Z", 0.0);

        assertThatThrownBy(() -> trainerWorkloadService.processWorkload(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("trainingDuration must be greater than zero");
    }

    @Test
    void getTrainerWorkloadSummary_groupsYearsAndMonthsAndMapsNullDurationToZero() {
        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setUsername("jdoe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        WorkloadMonth december2023 = new WorkloadMonth();
        december2023.setTrainerUsername("jdoe");
        december2023.setYear(2023);
        december2023.setMonth(12);
        december2023.setTrainingDuration(1.5);

        WorkloadMonth january2024 = new WorkloadMonth();
        january2024.setTrainerUsername("jdoe");
        january2024.setYear(2024);
        january2024.setMonth(1);
        january2024.setTrainingDuration(null);

        WorkloadMonth march2024 = new WorkloadMonth();
        march2024.setTrainerUsername("jdoe");
        march2024.setYear(2024);
        march2024.setMonth(3);
        march2024.setTrainingDuration(2.25);

        when(trainerWorkloadRepository.findByUsername("jdoe")).thenReturn(Optional.of(trainer));
        when(workloadMonthRepository.findAllByTrainerUsernameOrderByYearAscMonthAsc("jdoe"))
                .thenReturn(List.of(december2023, january2024, march2024));

        TrainerWorkloadSummaryResponse response = trainerWorkloadService.getTrainerWorkloadSummary("jdoe", null, null);

        assertThat(response.getTrainerUsername()).isEqualTo("jdoe");
        assertThat(response.getTrainerFirstName()).isEqualTo("John");
        assertThat(response.getTrainerLastName()).isEqualTo("Doe");
        assertThat(response.isTrainerStatus()).isTrue();
        assertThat(response.getYears()).hasSize(2);
        assertThat(response.getYears().get(0).getYear()).isEqualTo(2023);
        assertThat(response.getYears().get(0).getMonths())
                .extracting(WorkloadMonthSummary::getMonth, WorkloadMonthSummary::getTrainingSummaryDuration)
                .containsExactly(tuple(12, 1.5));
        assertThat(response.getYears().get(1).getYear()).isEqualTo(2024);
        assertThat(response.getYears().get(1).getMonths())
                .extracting(WorkloadMonthSummary::getMonth, WorkloadMonthSummary::getTrainingSummaryDuration)
                .containsExactly(
                        tuple(1, 0.0),
                        tuple(3, 2.25)
                );
    }

    @Test
    void getTrainerWorkloadSummary_returnsExplicitZeroMonthWhenRequestedPeriodMissing() {
        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setUsername("jdoe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(false);

        when(trainerWorkloadRepository.findByUsername("jdoe")).thenReturn(Optional.of(trainer));
        when(workloadMonthRepository.findAllByTrainerUsernameOrderByYearAscMonthAsc("jdoe")).thenReturn(List.of());

        TrainerWorkloadSummaryResponse response = trainerWorkloadService.getTrainerWorkloadSummary("jdoe", 2025, 4);

        assertThat(response.getYears()).hasSize(1);
        assertThat(response.getYears().get(0).getYear()).isEqualTo(2025);
        assertThat(response.getYears().get(0).getMonths())
                .extracting(WorkloadMonthSummary::getMonth, WorkloadMonthSummary::getTrainingSummaryDuration)
                .containsExactly(tuple(4, 0.0));
    }

    @Test
    void getTrainerWorkloadSummary_throwsNotFoundWhenTrainerMissing() {
        when(trainerWorkloadRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerWorkloadService.getTrainerWorkloadSummary("missing", null, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Trainer not found: missing");
    }

    @Test
    void getTrainerMonthlyWorkload_returnsZeroWhenMonthMissing() {
        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setUsername("jdoe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        when(trainerWorkloadRepository.findByUsername("jdoe")).thenReturn(Optional.of(trainer));
        when(workloadMonthRepository.findByTrainerUsernameAndYearAndMonth("jdoe", 2024, 6)).thenReturn(Optional.empty());

        TrainerMonthlyWorkloadResponse response = trainerWorkloadService.getTrainerMonthlyWorkload("jdoe", 2024, 6);

        assertThat(response.getTrainerUsername()).isEqualTo("jdoe");
        assertThat(response.getYear()).isEqualTo(2024);
        assertThat(response.getMonth()).isEqualTo(6);
        assertThat(response.getTrainingSummaryDuration()).isEqualTo(0.0);
    }

    private static TrainerWorkloadRequest createRequest(
            String username,
            String firstName,
            String lastName,
            boolean active,
            ActionType actionType,
            String trainingDate,
            Double duration
    ) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainerFirstName(firstName);
        request.setTrainerLastName(lastName);
        request.setActive(active);
        request.setActionType(actionType);
        request.setTrainingDate(Date.from(Instant.parse(trainingDate)));
        request.setTrainingDuration(duration);
        return request;
    }

}
