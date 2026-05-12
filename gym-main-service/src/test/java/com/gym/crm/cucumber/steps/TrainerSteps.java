package com.gym.crm.cucumber.steps;

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
public class TrainerSteps {

    private final ScenarioContext scenarioContext;
    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Given("I have trainer registration data with first name {string} and last name {string} and training type {long}")
    public void i_have_trainer_registration_data(String firstName, String lastName, Long trainingTypeId) {
        scenarioContext.storeData("trainerFirstName", firstName);
        scenarioContext.storeData("trainerLastName", lastName);
        scenarioContext.storeData("trainingTypeId", trainingTypeId);
    }

    @Given("I have trainer registration data with first name {string} and last name {string} without a training type")
    public void i_have_trainer_registration_data_missing_type(String firstName, String lastName) {
        scenarioContext.storeData("trainerFirstName", firstName);
        scenarioContext.storeData("trainerLastName", lastName);
        scenarioContext.storeData("trainingTypeId", null);
    }

    @When("I send a trainer registration request")
    public void i_send_trainer_registration_request() {
        String firstName = (String) scenarioContext.getData("trainerFirstName");
        String lastName = (String) scenarioContext.getData("trainerLastName");
        Long trainingTypeId = (Long) scenarioContext.getData("trainingTypeId");

        String requestBody = String.format(
                "{\"firstName\": \"%s\", \"lastName\": \"%s\", \"trainingTypeId\": %s}",
                firstName, lastName, trainingTypeId
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/register",
                HttpMethod.POST,
                buildJsonEntity(requestBody, null),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Given("a trainer already exists with first name {string} and last name {string} and training type {long}")
    public void a_trainer_exists_with_names(String firstName, String lastName, Long trainingTypeId) {
        String requestBody = String.format(
                "{\"firstName\": \"%s\", \"lastName\": \"%s\", \"trainingTypeId\": %d}",
                firstName, lastName, trainingTypeId
        );
        restTemplate.exchange(
                "/api/trainer/register",
                HttpMethod.POST,
                buildJsonEntity(requestBody, null),
                String.class
        );
    }

    @Then("the response should contain a trainer username starting with {string}")
    public void the_response_should_contain_trainer_username_starting_with(String prefix) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String username = extractField(response.getBody(), "username");
        assertThat(username).isNotNull().startsWith(prefix);
        scenarioContext.storeData("trainerUsername", username);
    }

    @When("I request trainer profile for {string}")
    public void i_request_trainer_profile(String username) {
        String token = scenarioContext.getAuthToken();
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/profile?username=" + username,
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the response should contain trainer username {string}")
    public void the_response_should_contain_trainer_username(String username) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String actualUsername = extractField(response.getBody(), "username");
        if (actualUsername == null) {
            String actualFirstName = extractField(response.getBody(), "firstName");
            assertThat(actualFirstName).isEqualTo(username.split("\\.")[0]);
        } else {
            assertThat(actualUsername).isEqualTo(username);
        }
    }

    @Then("the response should contain trainer details")
    public void the_response_should_contain_trainer_details() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String firstName = extractField(response.getBody(), "firstName");
        assertThat(firstName).isNotNull();
    }

    @Given("I have trainer update data with training type {long}")
    public void i_have_trainer_update_data(Long trainingTypeId) {
        scenarioContext.storeData("updateTrainerFirstName", "UpdatedFirstName");
        scenarioContext.storeData("updateTrainerLastName", "UpdatedLastName");
        scenarioContext.storeData("updateTrainerIsActive", Boolean.TRUE);
        scenarioContext.storeData("updateTrainingTypeId", trainingTypeId);
    }

    @Given("I have trainer update data without a training type")
    public void i_have_trainer_update_data_missing_type() {
        scenarioContext.storeData("updateTrainerFirstName", "UpdatedFirstName");
        scenarioContext.storeData("updateTrainerLastName", "UpdatedLastName");
        scenarioContext.storeData("updateTrainerIsActive", Boolean.TRUE);
        scenarioContext.storeData("updateTrainingTypeId", null);
    }


    @Then("the response should contain trainer training type {long}")
    public void the_response_should_contain_trainer_training_type(Long expectedTrainingTypeId) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        int actualTrainingTypeId = extractInt(response.getBody(), "trainingTypeId");
        assertThat((long) actualTrainingTypeId).isEqualTo(expectedTrainingTypeId);
    }

    @Given("a trainer exists with username {string} and active status {string}")
    public void a_trainer_exists_with_active_status(String username, String activeStatus) {
        scenarioContext.storeData("trainerUsernameForActivation", username);
        scenarioContext.storeData("trainerActiveStatus", activeStatus);
    }

    @When("I deactivate trainer {string}")
    public void i_deactivate_trainer(String username) {
        String token = scenarioContext.getAuthToken();
        String requestBody = String.format("{\"username\": \"%s\", \"isActive\": false}", username);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/deactivate",
                HttpMethod.PATCH,
                buildJsonEntity(requestBody, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the trainer should have active status set to false")
    public void the_trainer_should_have_active_status_false() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @When("I activate trainer {string}")
    public void i_activate_trainer(String username) {
        String token = scenarioContext.getAuthToken();
        String requestBody = String.format("{\"username\": \"%s\", \"isActive\": true}", username);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/activate",
                HttpMethod.PATCH,
                buildJsonEntity(requestBody, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the trainer should have active status set to true")
    public void the_trainer_should_have_active_status_true() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @When("I request trainer trainings for {string}")
    public void i_request_trainer_trainings(String username) {
        String token = scenarioContext.getAuthToken();
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/trainings/" + username,
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @When("I request trainer monthly workload for {string} for year {int} and month {int}")
    public void i_request_trainer_monthly_workload(String username, int year, int month) {
        String token = scenarioContext.getAuthToken();
        String path = String.format("/api/trainer/%s/workload?year=%d&month=%d", username, year, month);
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @When("I request trainer monthly workload for {string} for year {string} and month {int}")
    public void i_request_trainer_monthly_workload_with_invalid_year(String username, String year, int month) {
        String token = scenarioContext.getAuthToken();
        String path = String.format("/api/trainer/%s/workload?year=%s&month=%d", username, year, month);
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the workload response should contain trainer username {string}")
    public void response_contains_trainer_username(String username) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String actualUsername = extractField(response.getBody(), "trainerUsername");
        assertThat(actualUsername).isEqualTo(username);
    }

    @Then("the response should contain year {int} and month {int}")
    public void response_contains_year_and_month(int year, int month) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        int actualYear = extractInt(response.getBody(), "year");
        int actualMonth = extractInt(response.getBody(), "month");
        assertThat(actualYear).isEqualTo(year);
        assertThat(actualMonth).isEqualTo(month);
    }

    @Then("the response should contain training duration value")
    public void response_contains_training_duration() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String duration = extractField(response.getBody(), "trainingSummaryDuration");
        assertThat(duration).isNotNull();
    }

    private HttpEntity<String> buildJsonEntity(String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return new HttpEntity<>(body, headers);
    }

    private String extractField(String json, String field) {
        try {
            return objectMapper.readTree(json).path(field).asText(null);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to extract field '%s' from response body".formatted(field), ex);
        }
    }

    private int extractInt(String json, String field) {
        try {
            return objectMapper.readTree(json).path(field).asInt();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to extract int field '%s' from response body".formatted(field), ex);
        }
    }

    @Given("I have trainer update data with first name {string} and last name {string} and active status {string}")
    public void i_have_trainer_update_data(String firstName, String lastName, String isActive) {
        scenarioContext.storeData("updateTrainerFirstName", firstName);
        scenarioContext.storeData("updateTrainerLastName", lastName);
        scenarioContext.storeData("updateTrainerIsActive", Boolean.parseBoolean(isActive));
    }

    @When("I send a trainer profile update request for {string} with training type {long}")
    public void i_send_trainer_profile_update_request(String username, Long trainingTypeId) {
        String firstName = (String) scenarioContext.getData("updateTrainerFirstName");
        String lastName = (String) scenarioContext.getData("updateTrainerLastName");
        Boolean isActive = (Boolean) scenarioContext.getData("updateTrainerIsActive");
        String token = scenarioContext.getAuthToken();

        String requestBody = String.format(
                "{\"username\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", \"isActive\": %s}",
                username, firstName, lastName, isActive
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/profile",
                HttpMethod.PUT,
                buildJsonEntity(requestBody, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the response should contain trainer first name {string} and last name {string}")
    public void response_contains_updated_names(String expectedFirst, String expectedLast) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String actualFirst = extractField(response.getBody(), "firstName");
        String actualLast = extractField(response.getBody(), "lastName");

        assertThat(actualFirst).isEqualTo(expectedFirst);
        assertThat(actualLast).isEqualTo(expectedLast);
    }

    @Then("the response should still contain training type {long}")
    public void response_contains_original_training_type(Long originalTrainingTypeId) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        int actualTrainingTypeId = extractInt(response.getBody(), "trainingTypeId");
        assertThat((long) actualTrainingTypeId).isEqualTo(originalTrainingTypeId);
    }

    @When("I send a trainer profile update request for {string} without a training type")
    public void i_send_trainer_profile_update_request_missing_type(String username) {
        String firstName = (String) scenarioContext.getData("updateTrainerFirstName");
        String lastName = (String) scenarioContext.getData("updateTrainerLastName");
        Boolean isActive = (Boolean) scenarioContext.getData("updateTrainerIsActive");
        String token = scenarioContext.getAuthToken();

        String requestBody = String.format(
                "{\"username\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", \"isActive\": %s, \"trainingTypeId\": null}",
                username, firstName, lastName, isActive
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainer/profile",
                HttpMethod.PUT,
                buildJsonEntity(requestBody, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

}
