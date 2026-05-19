package com.gym.crm.trainerworkload.nosql;

import com.gym.crm.trainerworkload.model.ActionType;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerTrainingSummaryServiceTest {

    @Mock
    private TrainerTrainingSummaryRepository repository;

    @InjectMocks
    private TrainerTrainingSummaryService service;

    @BeforeEach
    void setUp() {
        String transactionId = "test-tx-" + System.currentTimeMillis();
        MDC.put("transactionId", transactionId);
    }

    @Test
    void processEvent_createsNewDocumentWhenTrainerNotFound() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", 2.5);
        when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
        when(repository.findByUsername("jdoe")).thenReturn(Optional.empty());

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("jdoe");
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.isStatus()).isTrue();
        assertThat(saved.getYears()).hasSize(1);
        assertThat(saved.getYears().get(0).getYear()).isEqualTo(2024);
        assertThat(saved.getYears().get(0).getMonths()).hasSize(1);
        assertThat(saved.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(6);
        assertThat(saved.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isEqualTo(2.5);
    }

    @Test
    void processEvent_createsNewDocWithNegativeDeltaClamped() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.DELETE, "2024-06-15", 5.0);
        when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
        when(repository.findByUsername("jdoe")).thenReturn(Optional.empty());

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        assertThat(saved.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isZero();
    }


    @Test
    void processEvent_addsNewYearToExistingTrainer() {
        TrainerTrainingSummary existing = new TrainerTrainingSummary();
        existing.setUsername("jdoe");
        existing.setFirstName("John");
        existing.setLastName("Doe");
        existing.setStatus(true);

        YearSummary year2023 = new YearSummary(2023, new ArrayList<>());
        year2023.getMonths().add(new MonthSummary(5, 3.0));
        existing.setYears(new ArrayList<>());
        existing.getYears().add(year2023);

         TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", 2.5);
         when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
         when(repository.findByUsername("jdoe")).thenReturn(Optional.of(existing));

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        assertThat(saved.getYears()).hasSize(2);
        YearSummary newYear = saved.getYears().stream().filter(y -> y.getYear() == 2024).findFirst().orElseThrow();
        assertThat(newYear.getMonths()).hasSize(1);
        assertThat(newYear.getMonths().get(0).getMonth()).isEqualTo(6);
        assertThat(newYear.getMonths().get(0).getTrainingSummaryDuration()).isEqualTo(2.5);
    }

     @Test
     void processEvent_addsNewMonthToExistingYear() {
         TrainerTrainingSummary existing = new TrainerTrainingSummary();
         existing.setUsername("jdoe");
         existing.setFirstName("John");
         existing.setLastName("Doe");
         existing.setStatus(true);

         YearSummary year2024 = new YearSummary(2024, new ArrayList<>());
         year2024.getMonths().add(new MonthSummary(5, 3.0));
         existing.setYears(new ArrayList<>());
         existing.getYears().add(year2024);

         TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", 1.5);
         when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
         when(repository.findByUsername("jdoe")).thenReturn(Optional.of(existing));

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        assertThat(saved.getYears()).hasSize(1);
        assertThat(saved.getYears().get(0).getMonths()).hasSize(2);
        MonthSummary newMonth = saved.getYears().get(0).getMonths().stream()
                .filter(m -> m.getMonth() == 6).findFirst().orElseThrow();
        assertThat(newMonth.getTrainingSummaryDuration()).isEqualTo(1.5);
    }


     @Test
     void processEvent_updatesExistingMonthWithADD() {
         TrainerTrainingSummary existing = new TrainerTrainingSummary();
         existing.setUsername("jdoe");
         existing.setFirstName("John");
         existing.setLastName("Doe");
         existing.setStatus(true);

         MonthSummary month6 = new MonthSummary(6, 2.0);
         YearSummary year2024 = new YearSummary(2024, new ArrayList<>());
         year2024.getMonths().add(month6);
         existing.setYears(new ArrayList<>());
         existing.getYears().add(year2024);

         TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", 1.5);
         when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
         when(repository.findByUsername("jdoe")).thenReturn(Optional.of(existing));

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        MonthSummary updatedMonth = saved.getYears().get(0).getMonths().get(0);
        assertThat(updatedMonth.getTrainingSummaryDuration()).isEqualTo(3.5); // 2.0 + 1.5
    }


     @Test
     void processEvent_updatesExistingMonthWithDelete() {
         TrainerTrainingSummary existing = new TrainerTrainingSummary();
         existing.setUsername("jdoe");
         existing.setFirstName("John");
         existing.setLastName("Doe");
         existing.setStatus(true);

         MonthSummary month6 = new MonthSummary(6, 2.0);
         YearSummary year2024 = new YearSummary(2024, new ArrayList<>());
         year2024.getMonths().add(month6);
         existing.setYears(new ArrayList<>());
         existing.getYears().add(year2024);

         TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.DELETE, "2024-06-15", 1.0);
         when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
         when(repository.findByUsername("jdoe")).thenReturn(Optional.of(existing));

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        MonthSummary updatedMonth = saved.getYears().get(0).getMonths().get(0);
        assertThat(updatedMonth.getTrainingSummaryDuration()).isEqualTo(1.0); // 2.0 - 1.0
    }

     @Test
     void processEvent_deleteBelowZeroClampsToZero() {
         TrainerTrainingSummary existing = new TrainerTrainingSummary();
         existing.setUsername("jdoe");
         existing.setFirstName("John");
         existing.setLastName("Doe");
         existing.setStatus(true);

         MonthSummary month6 = new MonthSummary(6, 2.0);
         YearSummary year2024 = new YearSummary(2024, new ArrayList<>());
         year2024.getMonths().add(month6);
         existing.setYears(new ArrayList<>());
         existing.getYears().add(year2024);

         TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.DELETE, "2024-06-15", 5.0);
         when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
         when(repository.findByUsername("jdoe")).thenReturn(Optional.of(existing));

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());

        TrainerTrainingSummary saved = captor.getValue();
        MonthSummary updatedMonth = saved.getYears().get(0).getMonths().get(0);
        assertThat(updatedMonth.getTrainingSummaryDuration()).isZero(); // max(0, 2.0 - 5.0) = 0
    }

    @Test
    void processEvent_throwsOnNullUsername() {
        TrainerWorkloadRequest request = createRequest(null, "John", "Doe", true, ActionType.ADD, "2024-06-15", 2.5);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainerUsername must not be blank");
    }

    @Test
    void processEvent_throwsOnBlankFirstName() {
        TrainerWorkloadRequest request = createRequest("jdoe", "", "Doe", true, ActionType.ADD, "2024-06-15", 2.5);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainerFirstName must not be blank");
    }

    @Test
    void processEvent_throwsOnNullLastName() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", null, true, ActionType.ADD, "2024-06-15", 2.5);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainerLastName must not be blank");
    }

    @Test
    void processEvent_throwsOnNullTrainingDate() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, null, 2.5);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDate must not be null");
    }

    @Test
    void processEvent_throwsOnNullTrainingDuration() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("jdoe");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setActionType(ActionType.ADD);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(null);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDuration must be positive");
    }

    @Test
    void processEvent_throwsOnZeroTrainingDuration() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", 0.0);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDuration must be positive");
    }

    @Test
    void processEvent_throwsOnNegativeTrainingDuration() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", -1.5);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDuration must be positive");
    }

    @Test
    void processEvent_throwsOnNullActionType() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, null, "2024-06-15", 2.5);

        assertThatThrownBy(() -> service.processEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("actionType must not be null");
    }

    @Test
    void processEvent_uses_atomicUpdateWhenSuccessful() {
        TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", true, ActionType.ADD, "2024-06-15", 2.5);

        when(repository.incrementMonthlyDuration(2.5, 2024, 6)).thenReturn(true);

        service.processEvent(request);

        verify(repository).incrementMonthlyDuration(2.5, 2024, 6);
        verify(repository, never()).findByUsername(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void processEvent_keepsStatusInSync() {
        TrainerTrainingSummary existing = new TrainerTrainingSummary();
        existing.setUsername("jdoe");
        existing.setStatus(true);

        YearSummary year2024 = new YearSummary(2024, new ArrayList<>());
        year2024.getMonths().add(new MonthSummary(6, 2.0));
        existing.setYears(new ArrayList<>());
        existing.getYears().add(year2024);

         TrainerWorkloadRequest request = createRequest("jdoe", "John", "Doe", false, ActionType.ADD, "2024-06-15", 1.0);
         when(repository.incrementMonthlyDuration(anyDouble(), anyInt(), anyInt())).thenReturn(false);
         when(repository.findByUsername("jdoe")).thenReturn(Optional.of(existing));

        service.processEvent(request);

        ArgumentCaptor<TrainerTrainingSummary> captor = ArgumentCaptor.forClass(TrainerTrainingSummary.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isStatus()).isFalse();
    }

    private TrainerWorkloadRequest createRequest(String username, String firstName, String lastName,
                                                 boolean active, ActionType actionType, String dateStr,
                                                 Double duration) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainerFirstName(firstName);
        request.setTrainerLastName(lastName);
        request.setActive(active);
        request.setActionType(actionType);

        if (dateStr != null) {
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr + "T00:00:00Z");
            request.setTrainingDate(Date.from(zdt.toInstant()));
        } else {
            request.setTrainingDate(null);
        }

        request.setTrainingDuration(duration);
        return request;
    }
}
