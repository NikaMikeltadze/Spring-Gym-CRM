package com.gym.crm.trainerworkload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.trainerworkload.model.ActionType;
import com.gym.crm.trainerworkload.model.TrainerMonthlyWorkloadResponse;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import com.gym.crm.trainerworkload.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new TrainerWorkloadController(trainerWorkloadService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void processWorkload_returnsOkAndDelegatesToService() throws Exception {
        TrainerWorkloadRequest request = createRequest();
        doNothing().when(trainerWorkloadService).processWorkload(any());

        mockMvc.perform(post("/api/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerWorkloadService).processWorkload(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(request);
    }

    @Test
    void processWorkload_returnsBadRequestForInvalidBody() throws Exception {
        TrainerWorkloadRequest request = createRequest();
        request.setTrainingDate(null);

        mockMvc.perform(post("/api/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("trainingDate must not be null"));
    }

    @Test
    void getTrainerMonthlyWorkload_returnsMappedResponse() throws Exception {
        TrainerMonthlyWorkloadResponse response = new TrainerMonthlyWorkloadResponse("jdoe", 2024, 6, 7.25);
        when(trainerWorkloadService.getTrainerMonthlyWorkload("jdoe", 2024, 6)).thenReturn(response);

        mockMvc.perform(get("/api/workloads/{trainerUsername}/workload", "jdoe")
                        .param("year", "2024")
                        .param("month", "6")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("jdoe"))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.month").value(6))
                .andExpect(jsonPath("$.trainingSummaryDuration").value(7.25));

        verify(trainerWorkloadService).getTrainerMonthlyWorkload("jdoe", 2024, 6);
    }

    @Test
    void getTrainerMonthlyWorkload_returnsNotFoundWhenServiceThrowsResponseStatusException() throws Exception {
        when(trainerWorkloadService.getTrainerMonthlyWorkload("missing", 2024, 6))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: missing"));

        mockMvc.perform(get("/api/workloads/{trainerUsername}/workload", "missing")
                        .param("year", "2024")
                        .param("month", "6")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found: missing"));
    }

    @Test
    void getTrainerMonthlyWorkload_returnsBadRequestForInvalidYearParameter() throws Exception {
        mockMvc.perform(get("/api/workloads/{trainerUsername}/workload", "jdoe")
                        .param("year", "not-a-number")
                        .param("month", "6"))
                .andExpect(status().isBadRequest());
    }

    private static TrainerWorkloadRequest createRequest() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("jdoe");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(Date.from(Instant.parse("2024-06-15T12:00:00Z")));
        request.setTrainingDuration(2.5);
        request.setActionType(ActionType.ADD);
        return request;
    }
}
