package com.gym.crm.controller;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dto.response.trainee.GetTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.GetTraineeTrainingsResponse;
import com.gym.crm.dto.response.trainee.RegisterTraineeResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeProfileResponse;
import com.gym.crm.dto.response.trainee.UpdateTraineeTrainerListResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeControllerRestAssuredTest extends RestAssuredControllerTestSupport {

    @Mock
    private GymFacade gymFacade;

    @Mock
    private TraineeDao traineeDao;

    @BeforeEach
    void setUp() {
        configureMockMvc(new TraineeController(gymFacade));
    }

    @Test
    void registerTrainee_ReturnsCreated() {
        when(gymFacade.createTrainee(any())).thenReturn(RegisterTraineeResponse.builder()
                .username("jane.doe")
                .password("generatedPass")
                .build());

        given()
                .contentType(JSON)
                .body("""
                        {
                          "firstName": "Jane",
                          "lastName": "Doe",
                          "dateOfBirth": "1990-01-01",
                          "address": "Baker Street"
                        }
                        """)
                .when()
                .post("/api/trainee/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("jane.doe"));
    }

    @Test
    void getTraineeProfile_ReturnsOk() {
        when(gymFacade.getTraineeByUsername("jane.doe")).thenReturn(GetTraineeProfileResponse.builder()
                .firstName("Jane")
                .lastName("Doe")
                .isActive(true)
                .build());

        given()
                .when()
                .get("/api/trainee/profile/jane.doe")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Jane"));
    }

    @Test
    void updateTraineeProfile_ReturnsOk() {
        when(gymFacade.updateTrainee(any())).thenReturn(UpdateTraineeProfileResponse.builder()
                .username("jane.doe")
                .firstName("Jane")
                .lastName("Doe")
                .isActive(true)
                .build());

        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "jane.doe",
                          "firstName": "Jane",
                          "lastName": "Doe",
                          "isActive": true
                        }
                        """)
                .when()
                .put("/api/trainee/profile")
                .then()
                .statusCode(200)
                .body("username", equalTo("jane.doe"));
    }

    @Test
    void deleteTraineeProfile_ReturnsNoContent() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "jane.doe"
                        }
                        """)
                .when()
                .delete("/api/trainee/profile")
                .then()
                .statusCode(204);

        verify(gymFacade).deleteTrainee("jane.doe");
    }

    @Test
    void activateTrainee_ReturnsOk() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "jane.doe",
                          "isActive": true
                        }
                        """)
                .when()
                .patch("/api/trainee/activate")
                .then()
                .statusCode(200);

        verify(gymFacade).activateTrainee(any());
    }

    @Test
    void deactivateTrainee_ReturnsOk() {
        given()
                .queryParam("isActive", false)
                .when()
                .patch("/api/trainee/deactivate/jane.doe")
                .then()
                .statusCode(200);

        verify(gymFacade).deactivateTrainee(argThat(req -> "jane.doe".equals(req.getUsername()) && !req.getIsActive()));
    }

    @Test
    void getTraineeTrainings_ReturnsOkAndUsesPathUsername() {
        when(gymFacade.getTraineeTrainings(any())).thenReturn(List.of(GetTraineeTrainingsResponse.builder()
                .trainingName("Morning Cardio")
                .build()));

        given()
                .queryParam("trainerName", "John")
                .when()
                .get("/api/trainee/trainings/jane.doe")
                .then()
                .statusCode(200)
                .body("[0].trainingName", equalTo("Morning Cardio"));

        verify(gymFacade).getTraineeTrainings(argThat(req -> "jane.doe".equals(req.getUsername())));
    }

    @Test
    void updateTraineeTrainerList_ReturnsOk() {
        when(gymFacade.updateTrainerList(any())).thenReturn(UpdateTraineeTrainerListResponse.builder()
                .trainerList(List.of(TrainerProfileInfo.builder()
                        .username("john.smith")
                        .firstName("John")
                        .lastName("Smith")
                        .trainingTypeId(1L)
                        .build()))
                .build());

        given()
                .contentType(JSON)
                .body("""
                        {
                          "traineeUsername": "jane.doe",
                          "trainerUsernameList": ["john.smith"]
                        }
                        """)
                .when()
                .put("/api/trainee/trainers")
                .then()
                .statusCode(200)
                .body("trainerList", hasSize(1))
                .body("trainerList[0].username", equalTo("john.smith"));
    }

    @Test
    void getUnassignedActiveTrainers_ReturnsOk() {
        when(gymFacade.getUnassignedActiveTrainers("jane.doe")).thenReturn(List.of(TrainerProfileInfo.builder()
                .username("john.smith")
                .firstName("John")
                .lastName("Smith")
                .trainingTypeId(1L)
                .build()));

        given()
                .when()
                .get("/api/trainee/unassigned-trainers/jane.doe")
                .then()
                .statusCode(200)
                .body("[0].username", equalTo("john.smith"));
    }

    @Test
    void updateTraineeProfile_ReturnsBadRequest_WhenPayloadIsInvalid() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "username": "",
                          "firstName": "",
                          "lastName": "",
                          "isActive": null
                        }
                        """)
                .when()
                .put("/api/trainee/profile")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"));
    }
}
