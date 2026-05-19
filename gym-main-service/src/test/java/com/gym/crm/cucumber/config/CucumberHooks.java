package com.gym.crm.cucumber.config;

import com.gym.crm.client.TrainerWorkloadClient;
import com.gym.crm.client.WorkloadSummaryResponse;
import com.gym.crm.cucumber.context.ScenarioContext;
import com.gym.crm.producer.TrainerWorkloadProducer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CucumberHooks {

    private final ScenarioContext scenarioContext;
    private final TrainerWorkloadProducer trainerWorkloadProducer;
    private final TrainerWorkloadClient trainerWorkloadClient;

    public CucumberHooks(ScenarioContext scenarioContext,
                         TrainerWorkloadProducer trainerWorkloadProducer,
                         TrainerWorkloadClient trainerWorkloadClient) {
        this.scenarioContext = scenarioContext;
        this.trainerWorkloadProducer = trainerWorkloadProducer;
        this.trainerWorkloadClient = trainerWorkloadClient;
    }

    @Before
    public void beforeScenario() {
        scenarioContext.clearContext();
        Mockito.reset(trainerWorkloadProducer, trainerWorkloadClient);
        when(trainerWorkloadClient.getMonthlyWorkload(anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> WorkloadSummaryResponse.builder()
                        .trainerUsername(invocation.getArgument(0))
                        .year(invocation.getArgument(1))
                        .month(invocation.getArgument(2))
                        .trainingSummaryDuration(0.0)
                        .build());
    }

    @After
    public void afterScenario() {
        scenarioContext.clearContext();
    }
}
