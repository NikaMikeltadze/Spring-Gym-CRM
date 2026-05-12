package com.gym.crm.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.cucumber.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class TrainingSteps {

    private final ScenarioContext scenarioContext;
    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Given("training type {string} exists")
    public void training_type_exists(String trainingType) {
        scenarioContext.storeData("trainingType", trainingType);
    }

    @Given("I have training data with trainee {string}, trainer {string}, type {string} and duration {double} hours")
    public void i_have_training_data(String traineeUsername, String trainerUsername, String trainingType, Double duration) {
        scenarioContext.storeData("trainingTrainee", traineeUsername);
        scenarioContext.storeData("trainingTrainer", trainerUsername);
        scenarioContext.storeData("trainingType", trainingType);
        scenarioContext.storeData("trainingDuration", duration);
    }

    @Given("I have training data with trainee {string}, trainer {string}, type {string}, duration {double} hours and date {string}")
    public void i_have_training_data_with_date(String traineeUsername, String trainerUsername, String trainingType, Double duration, String date) {
        i_have_training_data(traineeUsername, trainerUsername, trainingType, duration);
        scenarioContext.storeData("trainingDate", date);
    }

    @Given("I have training data with trainee {string}, trainer {string}, type {string} and no duration")
    public void i_have_training_data_no_duration(String traineeUsername, String trainerUsername, String trainingType) {
        scenarioContext.storeData("trainingTrainee", traineeUsername);
        scenarioContext.storeData("trainingTrainer", trainerUsername);
        scenarioContext.storeData("trainingType", trainingType);
        // no duration stored — resolves to null in the request body, triggering @NotNull validation
    }

    @When("I send an add training request")
    public void i_send_an_add_training_request() {
        String trainee = (String) scenarioContext.getData("trainingTrainee");
        String trainer = (String) scenarioContext.getData("trainingTrainer");
        String type = (String) scenarioContext.getData("trainingType");
        Double duration = (Double) scenarioContext.getData("trainingDuration");
        String date = (String) scenarioContext.getData("trainingDate");
        String token = scenarioContext.getAuthToken();

        String actualDate = date == null ? "2026-01-01" : date;
        String trainingName = (type == null || type.isBlank()) ? "BDD Training" : type;
        String body = String.format(
                "{\"traineeUsername\":\"%s\",\"trainerUsername\":\"%s\",\"trainingName\":\"%s\",\"trainingDate\":\"%s\",\"trainingDuration\":%s}",
                trainee, trainer, trainingName, actualDate, duration
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/training/add",
                HttpMethod.POST,
                buildJsonEntity(body, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @When("I request all training types")
    public void i_request_all_training_types() {
        String token = scenarioContext.getAuthToken();
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/training/types",
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the response should contain training details")
    public void the_response_should_contain_training_details() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        assertThat(response.getBody()).isNullOrEmpty();
    }

    @Then("the response should contain list of training types")
    public void the_response_should_contain_list_of_training_types() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        JsonNode trainingTypeList = extractNode(response.getBody(), "trainingTypeList");
        assertThat(trainingTypeList.isMissingNode()).isFalse();
    }

    @Then("the response should contain at least {int} training type")
    public void the_response_should_contain_at_least_training_type(int minimum) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        JsonNode trainingTypeList = extractNode(response.getBody(), "trainingTypeList");
        assertThat(trainingTypeList.size()).isGreaterThanOrEqualTo(minimum);
    }

    // --- helpers ---

    private HttpEntity<String> buildJsonEntity(String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return new HttpEntity<>(body, headers);
    }

    private JsonNode extractNode(String json, String field) {
        try {
            return objectMapper.readTree(json).path(field);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to extract node '%s' from response body".formatted(field), ex);
        }
    }
}
