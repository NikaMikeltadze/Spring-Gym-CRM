package com.gym.crm.controller;

import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingControllerRestAssuredTest extends RestAssuredControllerTestSupport {

    @Mock
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        configureMockMvc(new TrainingController(gymFacade));
    }

    @Test
    void addTraining_ReturnsCreated() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "traineeUsername": "jane.doe",
                          "trainerUsername": "john.smith",
                          "trainingName": "Morning Cardio",
                          "trainingDate": "2025-01-01",
                          "trainingDuration": 60.0
                        }
                        """)
        .when()
                .post("/api/training/add")
        .then()
                .statusCode(201);

        verify(gymFacade).createTraining(any());
    }

    @Test
    void getTrainingTypes_ReturnsOk() {
        when(gymFacade.getAllTrainings()).thenReturn(GetTrainingTypesResponse.builder()
                .trainingTypeList(List.of(TrainingTypeInfo.builder()
                        .trainingTypeId(1L)
                        .trainingTypeName("Cardio")
                        .build()))
                .build());

        given()
        .when()
                .get("/api/training/types")
        .then()
                .statusCode(200)
                .body("trainingTypeList", hasSize(1))
                .body("trainingTypeList[0].trainingTypeName", equalTo("Cardio"));
    }

    @Test
    void addTraining_ReturnsBadRequest_WhenPayloadIsInvalid() {
        given()
                .contentType(JSON)
                .body("""
                        {
                          "traineeUsername": "",
                          "trainerUsername": "",
                          "trainingName": "",
                          "trainingDate": null,
                          "trainingDuration": null
                        }
                        """)
        .when()
                .post("/api/training/add")
        .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"));
    }
}
