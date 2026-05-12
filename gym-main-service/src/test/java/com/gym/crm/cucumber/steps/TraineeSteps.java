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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class TraineeSteps {

    private final ScenarioContext scenarioContext;
    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Given("I have trainee registration data with first name {string} and last name {string} and date of birth {string}")
    public void i_have_trainee_registration_data(String firstName, String lastName, String dateOfBirth) {
        scenarioContext.storeData("traineeFirstName", firstName);
        scenarioContext.storeData("traineeLastName", lastName);
        scenarioContext.storeData("traineeDateOfBirth", LocalDate.parse(dateOfBirth));
    }

    @When("I send a trainee registration request")
    public void i_send_trainee_registration_request() {
        String firstName = (String) scenarioContext.getData("traineeFirstName");
        String lastName = (String) scenarioContext.getData("traineeLastName");
        LocalDate dateOfBirth = (LocalDate) scenarioContext.getData("traineeDateOfBirth");

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("dateOfBirth", dateOfBirth);

        String requestBody = serializeBody(body);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainee/register",
                HttpMethod.POST,
                buildJsonEntity(requestBody, null),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    private String serializeBody(Map<String, ?> body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize request body", ex);
        }
    }

    @Then("the response should contain a trainee username starting with {string}")
    public void the_response_should_contain_trainee_username_starting_with(String prefix) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String username = extractField(response.getBody(), "username");
        assertThat(username).isNotNull().startsWith(prefix);
        scenarioContext.storeData("traineeUsername", username);
    }

    @Given("a trainee already exists with first name {string} and last name {string}")
    public void a_trainee_exists_with_names(String firstName, String lastName) {
        scenarioContext.storeData("existingTraineeFirstName", firstName);
        scenarioContext.storeData("existingTraineeLastName", lastName);
    }

    @Given("a trainee exists with username {string}")
    public void a_trainee_exists_with_username(String username) {
        scenarioContext.storeData("existingTraineeUsername", username);
    }

    @When("I request trainee profile for {string}")
    public void i_request_trainee_profile(String username) {
        String token = scenarioContext.getAuthToken();
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainee/profile/" + username,
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the response should contain trainee username {string}")
    public void the_response_should_contain_trainee_username(String username) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String actualFirstName = extractField(response.getBody(), "firstName");
        assertThat(actualFirstName).isEqualTo(username.split("\\.")[0]);
    }

    @Then("the response should contain trainee details")
    public void the_response_should_contain_trainee_details() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String firstName = extractField(response.getBody(), "firstName");
        assertThat(firstName).isNotNull();
    }

    @Given("I have trainee update data with first name {string} and last name {string}")
    public void i_have_trainee_update_data(String firstName, String lastName) {
        scenarioContext.storeData("updateFirstName", firstName);
        scenarioContext.storeData("updateLastName", lastName);
    }

    @When("I send a trainee profile update request for {string}")
    public void i_send_trainee_profile_update_request(String username) {
        String firstName = (String) scenarioContext.getData("updateFirstName");
        String lastName = (String) scenarioContext.getData("updateLastName");
        String invalidDateOfBirth = (String) scenarioContext.getData("invalidDateOfBirth");
        String token = scenarioContext.getAuthToken();

        String dateOfBirthLine = invalidDateOfBirth == null
                ? "\"dateOfBirth\": null"
                : String.format("\"dateOfBirth\": \"%s\"", invalidDateOfBirth);

        String requestBody = String.format(
                "{\"username\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", %s, \"address\": null, \"isActive\": true}",
                username,
                firstName == null ? "Updated" : firstName,
                lastName == null ? "User" : lastName,
                dateOfBirthLine
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainee/profile",
                HttpMethod.PUT,
                buildJsonEntity(requestBody, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the response should contain trainee first name {string}")
    public void the_response_should_contain_trainee_first_name(String firstName) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String actualFirstName = extractField(response.getBody(), "firstName");
        assertThat(actualFirstName).isEqualTo(firstName);
    }

    @Given("I have trainee update data with invalid date of birth {string}")
    public void i_have_trainee_update_with_invalid_dob(String invalidDate) {
        scenarioContext.storeData("invalidDateOfBirth", invalidDate);
    }

    @When("I request trainee trainings for {string}")
    public void i_request_trainee_trainings(String username) {
        String token = scenarioContext.getAuthToken();
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainee/trainings/" + username,
                HttpMethod.GET,
                buildJsonEntity(null, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Given("a trainer exists with username {string}")
    public void a_trainer_exists_with_username(String username) {
        scenarioContext.storeData("existingTrainerUsername", username);
    }

    @When("I assign trainer {string} to trainee {string}")
    public void i_assign_trainer_to_trainee(String trainerUsername, String traineeUsername) {
        String token = scenarioContext.getAuthToken();
        String requestBody = String.format(
                "{\"traineeUsername\": \"%s\", \"trainerUsernameList\": [\"%s\"]}",
                traineeUsername, trainerUsername
        );
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trainee/trainers",
                HttpMethod.PUT,
                buildJsonEntity(requestBody, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
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
}
