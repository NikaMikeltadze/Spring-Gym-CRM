package com.gym.crm.cucumber.steps;

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

import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class ComponentTraineeSteps {

    private final TestRestTemplate restTemplate;
    private String requestJson;
    private ResponseEntity<String> response;

    @Given("a valid trainee registration request with first name {string} and last name {string}")
    public void validTraineeRequest(String firstName, String lastName) {
        requestJson = "{ \"firstName\": \"" + firstName + "\", \"lastName\": \"" + lastName + "\", \"dateOfBirth\": \"1990-01-01\", \"address\": \"123 Main St\" }";
    }

    @Given("a trainee registration request with missing last name")
    public void invalidTraineeRequest() {
        requestJson = "{ \"firstName\": \"Jane\", \"dateOfBirth\": \"1990-01-01\" }";
    }

    @When("the client posts to {string}")
    public void postToEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, new HttpEntity<>(requestJson, headers), String.class);
    }

    @Then("the status should be {int} or {int}")
    public void verifyStatus(int status1, int status2) {
        int current = response.getStatusCode().value();
        assertTrue(current == status1 || current == status2);
    }

    @Then("the status should be {int}")
    public void verifyStrictStatus(int status) {
        assertTrue(response.getStatusCode().value() == status);
    }

    @Then("the response should contain a generated username and password")
    public void verifyResponseContainsCredentials() {
        assertTrue(response.getBody().contains("username"));
        assertTrue(response.getBody().contains("password"));
    }
}