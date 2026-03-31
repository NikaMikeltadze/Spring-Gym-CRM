package com.gym.crm.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "security.cors")
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins = List.of("http://localhost:3000");

    @NotEmpty
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    @NotEmpty
    private List<String> allowedHeaders = List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With");

    private List<String> exposedHeaders = List.of("Authorization");

    private boolean allowCredentials = false;

    @Min(0)
    private long maxAgeSeconds = 3600;
}
