package com.gym.crm.trainerworkload.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.trainerworkload.cucumber.context.WorkloadTestContext;
import com.gym.crm.trainerworkload.model.TrainerWorkload;
import com.gym.crm.trainerworkload.model.WorkloadMonth;
import com.gym.crm.trainerworkload.repository.TrainerWorkloadRepository;
import com.gym.crm.trainerworkload.repository.WorkloadMonthRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class WorkloadSteps {
    @LocalServerPort
    private int port;

    private final WorkloadTestContext workloadTestContext;
    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final WorkloadMonthRepository workloadMonthRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("a trainer workload exists for trainer {string} in year {int} and month {int}")
    public void a_trainer_workload_exists(String username, Integer year, Integer month) {
        workloadMonthRepository
                .findByTrainerUsernameAndYearAndMonth(username, year, month).ifPresent(workloadMonthRepository::delete);

        TrainerWorkload trainer = trainerWorkloadRepository.findByUsername(username)
                .orElseGet(() -> {
                    TrainerWorkload newTrainer = new TrainerWorkload();
                    newTrainer.setUsername(username);
                    newTrainer.setFirstName("John");
                    newTrainer.setLastName("Smith");
                    newTrainer.setActive(true);
                    return newTrainer;
                });
        trainerWorkloadRepository.save(trainer);

        WorkloadMonth workloadMonth = new WorkloadMonth();
        workloadMonth.setTrainerUsername(username);
        workloadMonth.setYear(year);
        workloadMonth.setMonth(month);
        workloadMonth.setTrainingDuration(10.0);
        workloadMonthRepository.save(workloadMonth);

        workloadTestContext.setTrainerUsername(username);
        workloadTestContext.setYear(year);
        workloadTestContext.setMonth(month);
        workloadTestContext.setResponse(null);

    }

    @When("I request the monthly workload")
    public void i_request_the_monthly_workload() throws IOException, InterruptedException {
        workloadTestContext.setResponse(callWorkload(workloadTestContext.getYear(), workloadTestContext.getMonth()));
    }

    @When("I request the monthly workload with invalid month {int}")
    public void i_request_the_monthly_workload_with_invalid_month(Integer invalidMonth) throws IOException, InterruptedException {
        workloadTestContext.setResponse(callWorkload(workloadTestContext.getYear(), invalidMonth));
    }

    @When("I request the monthly workload for year {int} and month {int}")
    public void i_request_the_monthly_workload_for_year_and_month(Integer year, Integer month) throws IOException, InterruptedException {
        workloadTestContext.setResponse(callWorkload(year, month));
    }

    @When("I request the monthly workload with invalid year {int}")
    public void i_request_the_monthly_workload_with_invalid_year(Integer invalidYear) throws IOException, InterruptedException {
        workloadTestContext.setResponse(callWorkload(invalidYear, workloadTestContext.getMonth()));
    }

    @When("I request the monthly workload for trainer {string} in year {int} and month {int}")
    public void i_request_the_monthly_workload_for_trainer_in_year_and_month(String trainerUsername, Integer year, Integer month) throws IOException, InterruptedException {
        workloadTestContext.setResponse(callWorkloadForTrainer(trainerUsername, year, month));
    }

    @Then("the workload response status should be {int}")
    public void the_workload_response_status_should_be(Integer status) {
        assertThat(workloadTestContext.getResponse().getStatusCode().value()).isEqualTo(status);
    }

    @Then("the workload response should contain trainer username {string}")
    public void the_workload_response_should_contain_trainer_username(String username) {
        Map<String, Object> body = responseBodyAsMap();
        assertThat(body.get("trainerUsername")).isEqualTo(username);
    }

    @Then("the workload response should contain monthly duration {double}")
    public void the_workload_response_should_contain_monthly_duration(Double duration) {
        Map<String, Object> body = responseBodyAsMap();
        Number actualDuration = (Number) body.get("trainingSummaryDuration");
        assertThat(actualDuration.doubleValue()).isEqualTo(duration);
    }

    @Then("the workload response should contain year {int} and month {int}")
    public void the_workload_response_should_contain_year_and_month(Integer year, Integer month) {
        Map<String, Object> body = responseBodyAsMap();
        Number actualYear = (Number) body.get("year");
        Number actualMonth = (Number) body.get("month");
        assertThat(actualYear.intValue()).isEqualTo(year);
        assertThat(actualMonth.intValue()).isEqualTo(month);
    }

    @Then("the workload error response should contain message {string}")
    public void the_workload_error_response_should_contain_message(String expectedMessage) {
        Map<String, Object> body = responseBodyAsMap();
        assertThat(body.get("message")).isEqualTo(expectedMessage);
    }

    private ResponseEntity<Object> callWorkload(Integer yearParam, Integer monthParam) throws IOException, InterruptedException {
        return callWorkloadForTrainer(workloadTestContext.getTrainerUsername(), yearParam, monthParam);
    }

    private ResponseEntity<Object> callWorkloadForTrainer(String trainerUsername, Integer yearParam, Integer monthParam) throws IOException, InterruptedException {
        String url = "http://localhost:" + port + "/api/workloads/" + trainerUsername
                + "/workload?year=" + yearParam + "&month=" + monthParam;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return ResponseEntity.status(httpResponse.statusCode()).body(httpResponse.body());
    }

    private Map<String, Object> responseBodyAsMap() {
        assertThat(workloadTestContext.getResponse()).isNotNull();
        Object rawBody = workloadTestContext.getResponse().getBody();
        assertThat(rawBody).isInstanceOf(String.class);
        try {
            return objectMapper.readValue((String) rawBody, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse response body", e);
        }
    }

    @When("I delete the monthly workload")
    public void iDeleteTheMonthlyWorkload() {
        String trainerUsername = workloadTestContext.getTrainerUsername();
        Integer year = workloadTestContext.getYear();
        Integer month = workloadTestContext.getMonth();

        workloadMonthRepository.findByTrainerUsernameAndYearAndMonth(trainerUsername, year, month)
                .ifPresent(workloadMonthRepository::delete);

        if (workloadMonthRepository.findAllByTrainerUsernameOrderByYearAscMonthAsc(trainerUsername).isEmpty()) {
            trainerWorkloadRepository.findByUsername(trainerUsername)
                    .ifPresent(trainerWorkloadRepository::delete);
        }

        workloadTestContext.setResponse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
