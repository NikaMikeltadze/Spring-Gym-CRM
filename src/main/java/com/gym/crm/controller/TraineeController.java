package com.gym.crm.controller;

import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.response.ApiErrorResponse;
import com.gym.crm.dto.response.trainee.*;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainee")
@RequiredArgsConstructor
@Validated
@Tag(name = "Trainee", description = "Trainee profile and assignment endpoints")
@SecurityRequirement(name = "usernameHeader")
@SecurityRequirement(name = "passwordHeader")
public class TraineeController {
    private final GymFacade gymFacade;

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(summary = "Register a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee registered",
                    content = @Content(schema = @Schema(implementation = RegisterTraineeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid registration payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Trainee already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<RegisterTraineeResponse> registerTrainee(@Valid @RequestBody RegisterTraineeRequest traineeRequest) {
        RegisterTraineeResponse response = gymFacade.createTrainee(traineeRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/profile/{username}")
    @Operation(summary = "Get trainee profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile returned",
                    content = @Content(schema = @Schema(implementation = GetTraineeProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<GetTraineeProfileResponse> getTraineeProfile(@PathVariable String username) {
        GetTraineeProfileResponse response = gymFacade.getTraineeByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile updated",
                    content = @Content(schema = @Schema(implementation = UpdateTraineeProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<UpdateTraineeProfileResponse> updateTraineeProfile(@Valid @RequestBody UpdateTraineeProfileRequest request) {
        UpdateTraineeProfileResponse response = gymFacade.updateTrainee(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    @Operation(summary = "Delete trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee profile deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid delete payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteTraineeProfile(@Valid @RequestBody DeleteTraineeProfileRequest request) {
        gymFacade.deleteTrainee(request.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate")
    @Operation(summary = "Activate trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee activated"),
            @ApiResponse(responseCode = "400", description = "Invalid activation payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> activateTrainee(@Valid @RequestBody ActivateTraineeRequest request) {
        gymFacade.activateTrainee(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deactivate/{username}")
    @Operation(summary = "Deactivate trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deactivated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deactivateTrainee(@PathVariable String username, @RequestParam Boolean isActive) {
        gymFacade.deactivateTrainee(new DeactivateTraineeRequest(username, isActive));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainings/{username}")
    @Operation(summary = "Get trainee trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee trainings returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetTraineeTrainingsResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<GetTraineeTrainingsResponse>> getTraineeTrainings(@PathVariable String username,
                                                                                 @Valid @ModelAttribute GetTraineeTrainingsRequest request) {
        request.setUsername(username);
        List<GetTraineeTrainingsResponse> response = gymFacade.getTraineeTrainings(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/trainers")
    @Operation(summary = "Update trainee trainer list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer list updated",
                    content = @Content(schema = @Schema(implementation = UpdateTraineeTrainerListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<UpdateTraineeTrainerListResponse> updateTraineeTrainerList(@Valid @RequestBody UpdateTraineeTrainerListRequest request) {
        UpdateTraineeTrainerListResponse response = gymFacade.updateTrainerList(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned-trainers/{username}")
    @Operation(summary = "Get unassigned active trainers for trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unassigned trainers returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerProfileInfo.class)))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Parameter(name = "username", description = "Trainee username", required = true)
    public ResponseEntity<List<TrainerProfileInfo>> getUnassignedActiveTrainers(@PathVariable String username) {
        List<TrainerProfileInfo> response = gymFacade.getUnassignedActiveTrainers(username);
        return ResponseEntity.ok(response);
    }
}
