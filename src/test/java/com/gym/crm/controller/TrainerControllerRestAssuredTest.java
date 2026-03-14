package com.gym.crm.controller;

import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerRestAssuredTest extends RestAssuredControllerTestSupport {

    @Mock
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        configureMockMvc(new TrainerController(gymFacade));
    }

    @Test
    void registerTrainer_ReturnsCreated() {
        when(gymFacade.createTrainer(any())).thenReturn(RegisterTrainerResponse.builder()
                .username("john.smith")
                .password("generatedPass")
                .build());

        given()
                .contentType(JSON)
                .body("""
                        {
                          "firstName": "John",
                          "lastName": "Smith",
                          "trainingTypeId": 1
                        }
                        """)
        .when()
                .post("/api/trainer/register")
        .then()
                .statusCode(201)
                .body("username", equalTo("john.smith"));
    }

    @Test
    void getTrainerProfile_ReturnsOk() {
        when(gymFacade.getTrainerByUsername("john.smith")).thenReturn(Optional.of(GetTrainerProfileResponse.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .build()));

        given()
                .queryParam("username", "john.smith")
        .when()
                .get("/api/trainer/profile")
        .then()
                .statusCode(200)
                .body("firstName", equalTo("John"));
    }

    @Test
    void updateTrainerProfile_ReturnsOk() {
        when(gymFacade.updateTrainer(any())).thenReturn(UpdateTrainerProfileResponse.builder()
                .username("john.smith")
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .build());

        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "john.smith",
                          "firstName": "John",
                          "lastName": "Smith",
                          "isActive": true
                        }
                        """)
        .when()
                .put("/api/trainer/profile")
        .then()
                .statusCode(200)
                .body("username", equalTo("john.smith"));
    }

    @Test
    void changeTrainerPassword_ReturnsOk() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "john.smith",
                          "password": "oldPass",
                          "newPassword": "newPass"
                        }
                        """)
        .when()
                .put("/api/trainer/password")
        .then()
                .statusCode(200);

        verify(gymFacade).changeUserPassword(any());
    }

    @Test
    void activateTrainer_ReturnsOk() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "john.smith",
                          "isActive": true
                        }
                        """)
        .when()
                .patch("/api/trainer/activate")
        .then()
                .statusCode(200);

        verify(gymFacade).activateTrainer("john.smith");
    }

    @Test
    void deactivateTrainer_ReturnsOk() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "john.smith",
                          "isActive": false
                        }
                        """)
        .when()
                .patch("/api/trainer/deactivate")
        .then()
                .statusCode(200);

        verify(gymFacade).deactivateTrainer("john.smith");
    }

    @Test
    void getTrainerTrainings_ReturnsOkAndUsesPathUsername() {
        when(gymFacade.getTrainerTrainings(any())).thenReturn(List.of(GetTrainerTrainingsResponse.builder()
                .trainingName("Strength Session")
                .build()));

        given()
                .queryParam("traineeName", "Jane")
        .when()
                .get("/api/trainer/trainings/john.smith")
        .then()
                .statusCode(200)
                .body("[0].trainingName", equalTo("Strength Session"));
    }

    @Test
    void registerTrainer_ReturnsBadRequest_WhenPayloadIsInvalid() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "firstName": "",
                          "lastName": "",
                          "trainingTypeId": null
                        }
                        """)
        .when()
                .post("/api/trainer/register")
        .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"));
    }
}
