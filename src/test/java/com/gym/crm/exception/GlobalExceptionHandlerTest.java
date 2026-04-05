package com.gym.crm.exception;

import com.gym.crm.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.Mockito.mock;
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
                mock(NoResourceFoundException.class),
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
                mock(NoResourceFoundException.class),
                request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleAccountLocked_ReturnsLocked_WithMessageAndStatus() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        ResponseEntity<ApiErrorResponse> response = handler.handleAccountLocked(
                new AccountLockedException(java.time.Instant.parse("2026-03-31T10:05:00Z")),
                request
        );

        assertEquals(HttpStatus.LOCKED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("Account locked until 2026-03-31T10:05:00Z", response.getBody().getMessage());
        assertEquals(423, response.getBody().getStatus());
    }
}
