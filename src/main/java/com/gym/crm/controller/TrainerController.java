package com.gym.crm.controller;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainer.ActivateTrainerRequest;
import com.gym.crm.dto.request.trainer.DeactivateTrainerRequest;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.response.ApiErrorResponse;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.exception.NotFoundException;
import com.gym.crm.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Trainer", description = "Trainer profile and training endpoints")
@SecurityRequirement(name = "usernameHeader")
@SecurityRequirement(name = "passwordHeader")
public class TrainerController {
    private final GymFacade gymFacade;

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(summary = "Register a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered",
                    content = @Content(schema = @Schema(implementation = RegisterTrainerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid registration payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Trainer already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<RegisterTrainerResponse> registerTrainer(@Valid @RequestBody RegisterTrainerRequest trainerRequest) {
        log.info("Register trainer request received for firstName={} and lastName={}",
                trainerRequest.getFirstName(), trainerRequest.getLastName());
        RegisterTrainerResponse response = gymFacade.createTrainer(trainerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get trainer profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile returned",
                    content = @Content(schema = @Schema(implementation = GetTrainerProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<GetTrainerProfileResponse> getTrainerProfile(@NotBlank @RequestParam String username) {
        GetTrainerProfileResponse response = gymFacade.getTrainerByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found with username: " + username));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile updated",
                    content = @Content(schema = @Schema(implementation = UpdateTrainerProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<UpdateTrainerProfileResponse> updateTrainerProfile(@Valid @RequestBody UpdateTrainerProfileRequest request) {
        UpdateTrainerProfileResponse response = gymFacade.updateTrainer(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    @Operation(summary = "Change trainer password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Invalid password change payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> changeTrainerPassword(@Valid @RequestBody ChangeLoginRequest request) {
        gymFacade.changeUserPassword(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate")
    @Operation(summary = "Activate trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer activated"),
            @ApiResponse(responseCode = "400", description = "Invalid activation payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> activateTrainer(@Valid @RequestBody ActivateTrainerRequest request) {
        gymFacade.activateTrainer(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deactivate")
    @Operation(summary = "Deactivate trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer deactivated"),
            @ApiResponse(responseCode = "400", description = "Invalid deactivation payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deactivateTrainer(@Valid @RequestBody DeactivateTrainerRequest request) {
        gymFacade.deactivateTrainer(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainings/{username}")
    @Operation(summary = "Get trainings assigned to trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer trainings returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetTrainerTrainingsResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid auth headers",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<GetTrainerTrainingsResponse>> getTrainerTrainings(@PathVariable String username,
                                                                                  @Valid @ModelAttribute GetTrainerTrainingsRequest request) {
        request.setUsername(username);
        List<GetTrainerTrainingsResponse> response = gymFacade.getTrainerTrainings(request);
        return ResponseEntity.ok(response);
    }
}
