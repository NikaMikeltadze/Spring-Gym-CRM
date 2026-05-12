package com.gym.crm.cucumber.steps;

import com.gym.crm.config.auth.JwtTokenService;
import com.gym.crm.cucumber.context.ScenarioContext;
import com.gym.crm.producer.TrainerWorkloadProducer;
import io.cucumber.java.en.And;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
public class IntegrationSteps {

    private final ScenarioContext scenarioContext;
    private final TestRestTemplate restTemplate;
    private final JwtTokenService jwtTokenService;
    private final TrainerWorkloadProducer trainerWorkloadProducer;

    @Given("the workload service is up and expecting a workload update")
    public void the_workload_service_is_up_and_expecting_a_workload_update() {
        String token = jwtTokenService.generateToken("John.Smith");
        scenarioContext.setAuthToken(token);
    }

    @When("I save a new training in the main service")
    public void i_save_a_new_training_in_the_main_service() {
        String token = scenarioContext.getAuthToken();
        String body = """
                {
                  "traineeUsername": "Sarah.Williams",
                  "trainerUsername": "John.Smith",
                  "trainingName": "Yoga",
                  "trainingDate": "2026-01-15",
                  "trainingDuration": 1.5
                }
                """;
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/training/add",
                HttpMethod.POST,
                buildJsonEntity(body, token),
                String.class
        );
        scenarioContext.setLastResponse(response);
    }

    @Then("the main service returns a 201 success")
    public void the_main_service_returns_a_201_success() {
        assertThat(scenarioContext.getLastResponse().getStatusCode().value()).isEqualTo(201);
    }

    @And("the workload service received the request successfully")
    public void the_workload_service_received_the_request_successfully() {
        verify(trainerWorkloadProducer, atLeastOnce()).sendWorkloadUpdate(any());
    }

    private HttpEntity<String> buildJsonEntity(String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return new HttpEntity<>(body, headers);
    }
}