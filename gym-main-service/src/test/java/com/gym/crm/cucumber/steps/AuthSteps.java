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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class AuthSteps {
    private final ScenarioContext scenarioContext;
    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Given("I have valid credentials with username {string} and password {string}")
    public void i_have_valid_credentials(String username, String password) {
        scenarioContext.storeData("username", username);
        scenarioContext.storeData("password", password);
    }

    @Given("I have valid username {string} but invalid password {string}")
    public void i_have_valid_username_invalid_password(String username, String password) {
        scenarioContext.storeData("username", username);
        scenarioContext.storeData("password", password);
    }

    @Given("I have username {string} and password {string}")
    public void i_have_username_and_password(String username, String password) {
        scenarioContext.storeData("username", username);
        scenarioContext.storeData("password", password);
    }

    @When("I send a login request")
    public void i_send_a_login_request() {
        String username = (String) scenarioContext.getData("username");
        String password = (String) scenarioContext.getData("password");

        String requestBody = serializeBody(Map.of(
                "username", username,
                "password", password
        ));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                buildJsonEntity(requestBody, null),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the response should contain a JWT token")
    public void the_response_should_contain_jwt_token() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String token = extractField(response.getBody(), "token");
        assertThat(token).isNotNull().isNotEmpty();
        scenarioContext.storeData("authToken", token);
    }

    @Then("I should store the token for authenticated requests")
    public void i_should_store_the_token() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String token = extractField(response.getBody(), "token");
        assertThat(token).isNotNull();
        scenarioContext.setAuthToken(token);
    }

    @Given("I have current password {string} and new password {string}")
    public void i_have_current_and_new_password(String currentPassword, String newPassword) {
        scenarioContext.storeData("currentPassword", currentPassword);
        scenarioContext.storeData("newPassword", newPassword);
    }

    @When("I send a change password request")
    public void i_send_change_password_request() {
        String username = (String) scenarioContext.getData("username");
        String currentPassword = (String) scenarioContext.getData("currentPassword");
        String newPassword = (String) scenarioContext.getData("newPassword");
        String token = scenarioContext.getAuthToken();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("username", username != null ? username : "John.Smith");
        payload.put("password", currentPassword != null ? currentPassword : "");
        payload.put("newPassword", newPassword != null ? newPassword : "");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/change_password",
                HttpMethod.POST,
                buildJsonEntity(serializeBody(payload), token),
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

    private String serializeBody(Map<String, ?> body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize request body", ex);
        }
    }
}
