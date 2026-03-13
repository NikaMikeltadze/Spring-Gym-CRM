package com.gym.crm.controller;

import com.gym.crm.dto.request.LoginRequest;
import com.gym.crm.service.AuthenticationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void login_ReturnsOk_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john.doe");
        request.setPassword("securePass1");

        when(authenticationService.authenticate("john.doe", "securePass1")).thenReturn(true);

        ResponseEntity<Void> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        verify(authenticationService).authenticate("john.doe", "securePass1");
    }

    @Test
    void login_ReturnsUnauthorized_WhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john.doe");
        request.setPassword("wrongPass");

        when(authenticationService.authenticate("john.doe", "wrongPass")).thenReturn(false);

        ResponseEntity<Void> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());
        verify(authenticationService).authenticate("john.doe", "wrongPass");
    }

    @Test
    void login_ReturnsUnauthorized_WhenUserDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setUsername("missing.user");
        request.setPassword("securePass1");

        when(authenticationService.authenticate("missing.user", "securePass1"))
                .thenThrow(new IllegalArgumentException("User not found with username: missing.user"));

        ResponseEntity<Void> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());
        verify(authenticationService).authenticate("missing.user", "securePass1");
    }

    @Test
    void loginRequest_HasValidationErrors_WhenUsernameAndPasswordAreBlank() {
        LoginRequest request = new LoginRequest();
        request.setUsername("   ");
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(violation -> "username".equals(violation.getPropertyPath().toString())));
        assertTrue(violations.stream().anyMatch(violation -> "password".equals(violation.getPropertyPath().toString())));
    }
}
