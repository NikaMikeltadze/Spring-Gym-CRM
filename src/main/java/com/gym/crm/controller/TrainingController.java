package com.gym.crm.controller;

import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.ApiErrorResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/training")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training management endpoints")
@SecurityRequirement(name = "usernameHeader")
@SecurityRequirement(name = "passwordHeader")
public class TrainingController {
    private final GymFacade gymFacade;

    @PostMapping("/add")
    @Operation(summary = "Add a training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training created"),
            @ApiResponse(responseCode = "400", description = "Invalid training payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest request) {
        log.info("Add training request received for traineeUsername={} and trainerUsername={}", request.getTraineeUsername(), request.getTrainerUsername());
        gymFacade.createTraining(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/types")
    @Operation(summary = "Get available training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types returned",
                    content = @Content(schema = @Schema(implementation = GetTrainingTypesResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<GetTrainingTypesResponse> getTrainingTypes() {
        log.info("Get training types request received");
        GetTrainingTypesResponse response = gymFacade.getAllTrainings();
        return ResponseEntity.ok(response);
    }
}
