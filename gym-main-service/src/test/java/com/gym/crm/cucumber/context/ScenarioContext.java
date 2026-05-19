package com.gym.crm.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ScenarioScope
public class ScenarioContext {
    private ResponseEntity<String> lastResponse;
    private String authToken;
    private final Map<String, Object> extractedData = new HashMap<>();

    public void storeData(String key, Object value) {
        extractedData.put(key, value);
    }

    public Object getData(String key) {
        return extractedData.get(key);
    }

    public void clearContext() {
        lastResponse = null;
        authToken = null;
        extractedData.clear();
    }
}

