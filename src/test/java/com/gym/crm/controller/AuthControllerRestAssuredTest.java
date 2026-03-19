package com.gym.crm.controller;

import com.gym.crm.facade.GymFacade;
import com.gym.crm.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerRestAssuredTest extends RestAssuredControllerTestSupport {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        configureMockMvc(new AuthController(authenticationService, gymFacade));
    }

    @Test
    void login_ReturnsOk_WhenCredentialsAreValid() {
        when(authenticationService.authenticate("john.doe", "securePass1")).thenReturn(true);

        given()
                .queryParam("username", "john.doe")
                .queryParam("password", "securePass1")
        .when()
                .get("/api/auth/login")
        .then()
                .statusCode(200);

        verify(authenticationService).authenticate("john.doe", "securePass1");
    }

    @Test
    void login_ReturnsUnauthorized_WhenCredentialsAreInvalid() {
        when(authenticationService.authenticate("john.doe", "bad-pass")).thenReturn(false);

        given()
                .queryParam("username", "john.doe")
                .queryParam("password", "bad-pass")
        .when()
                .get("/api/auth/login")
        .then()
                .statusCode(401);

        verify(authenticationService).authenticate("john.doe", "bad-pass");
    }

    @Test
    void changeLogin_ReturnsOk_WhenPayloadIsValidAndCurrentPasswordMatches() {
        when(authenticationService.authenticate("john.doe", "securePass1")).thenReturn(true);

        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "john.doe",
                          "password": "securePass1",
                          "newPassword": "newPass2"
                        }
                        """)
        .when()
                .post("/api/auth")
        .then()
                .statusCode(200);

        verify(authenticationService).authenticate("john.doe", "securePass1");
    }

    @Test
    void changeLogin_ReturnsBadRequest_WhenPayloadIsInvalid() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "john.doe",
                          "password": "securePass1",
                          "newPassword": ""
                        }
                        """)
        .when()
                .post("/api/auth")
        .then()
                .statusCode(400)
                .body("fieldErrors.newPassword", org.hamcrest.Matchers.equalTo("New Password must not be blank"));
    }
}
