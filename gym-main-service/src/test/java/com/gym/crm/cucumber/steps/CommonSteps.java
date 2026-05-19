package com.gym.crm.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.cucumber.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CommonSteps {

    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;
    private final com.gym.crm.config.auth.JwtTokenService jwtTokenService;

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) {
        assertThat(scenarioContext.getLastResponse().getStatusCode().value()).isEqualTo(statusCode);
    }

    @Then("the response should contain message {string}")
    public void the_response_should_contain_message(String expectedMessage) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String actualMessage = extractField(response.getBody(), "message");
        assertThat(actualMessage).contains(expectedMessage);
    }

    @Then("the response should contain a temporary password")
    public void the_response_should_contain_temporary_password() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        String password = extractField(response.getBody(), "password");
        assertThat(password).isNotNull().isNotEmpty();
    }

    @Given("I have a valid JWT token for authenticated requests")
    public void i_have_valid_jwt_token() {
        String token = jwtTokenService.generateToken("John.Smith");
        scenarioContext.setAuthToken(token);
    }

    @Given("I am not authenticated")
    public void i_am_not_authenticated() {
        scenarioContext.setAuthToken(null);
    }

    @Then("the response should contain validation error for field {string}")
    public void the_response_should_contain_validation_error_for_field(String fieldName) {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        JsonNode fieldError = extractNode(response.getBody(), "fieldErrors").path(fieldName);
        assertThat(fieldError.isMissingNode()).isFalse();
    }

    @Then("the response should contain validation error")
    public void the_response_should_contain_validation_error() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        JsonNode fieldErrors = extractNode(response.getBody(), "fieldErrors");
        assertThat(fieldErrors.isMissingNode()).isFalse();
    }

    @Then("the response should contain a list of trainings")
    public void the_response_should_contain_list_of_trainings() {
        ResponseEntity<String> response = scenarioContext.getLastResponse();
        JsonNode root = parseBody(response.getBody());
        assertThat(root.isArray()).isTrue();
    }

    private String extractField(String json, String field) {
        try {
            return objectMapper.readTree(json).path(field).asText(null);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to extract field '%s' from response body".formatted(field), ex);
        }
    }

    private JsonNode extractNode(String json, String field) {
        try {
            return objectMapper.readTree(json).path(field);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to extract node '%s' from response body".formatted(field), ex);
        }
    }

    private JsonNode parseBody(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse response body", ex);
        }
    }
}
