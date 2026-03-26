package com.gym.crm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_ReturnsNotFoundSilently_ForRootPath() {
        when(request.getRequestURI()).thenReturn("/");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NoResourceFoundException(HttpMethod.GET, ""),
                request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleNotFound_ReturnsNotFound_ForMissingPrometheusEndpoint() {
        when(request.getRequestURI()).thenReturn("/actuator/prometheus");
        when(request.getMethod()).thenReturn("GET");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NoResourceFoundException(HttpMethod.GET, "actuator/prometheus"),
                request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
