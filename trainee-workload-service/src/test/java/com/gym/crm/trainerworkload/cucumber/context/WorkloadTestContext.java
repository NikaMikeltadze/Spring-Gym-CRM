package com.gym.crm.trainerworkload.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Data
public class WorkloadTestContext {
    private String trainerUsername;
    private Integer year;
    private Integer month;
    private ResponseEntity<Object> response;
}
