package com.gym.crm.controller;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainer.ActivateTrainerRequest;
import com.gym.crm.dto.request.trainer.DeactivateTrainerRequest;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.request.trainer.RegisterTrainerRequest;
import com.gym.crm.dto.request.trainer.UpdateTrainerProfileRequest;
import com.gym.crm.dto.response.trainer.GetTrainerProfileResponse;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.trainer.UpdateTrainerProfileResponse;
import com.gym.crm.exception.NotFoundException;
import com.gym.crm.facade.GymFacade;
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
public class TrainerController {
    private final GymFacade gymFacade;

    @PostMapping("/register")
    public ResponseEntity<RegisterTrainerResponse> registerTrainer(@Valid @RequestBody RegisterTrainerRequest trainerRequest) {
        log.info("Register trainer request received for firstName={} and lastName={}",
                trainerRequest.getFirstName(), trainerRequest.getLastName());
        RegisterTrainerResponse response = gymFacade.createTrainer(trainerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<GetTrainerProfileResponse> getTrainerProfile(@NotBlank @RequestParam String username) {
        GetTrainerProfileResponse response = gymFacade.getTrainerByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found with username: " + username));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UpdateTrainerProfileResponse> updateTrainerProfile(@Valid @RequestBody UpdateTrainerProfileRequest request) {
        UpdateTrainerProfileResponse response = gymFacade.updateTrainer(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changeTrainerPassword(@Valid @RequestBody ChangeLoginRequest request) {
        gymFacade.changeUserPassword(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate")
    public ResponseEntity<Void> activateTrainer(@Valid @RequestBody ActivateTrainerRequest request) {
        gymFacade.activateTrainer(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<Void> deactivateTrainer(@Valid @RequestBody DeactivateTrainerRequest request) {
        gymFacade.deactivateTrainer(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainings/{username}")
    public ResponseEntity<List<GetTrainerTrainingsResponse>> getTrainerTrainings(@PathVariable String username,
                                                                                  @Valid @ModelAttribute GetTrainerTrainingsRequest request) {
        request.setUsername(username);
        List<GetTrainerTrainingsResponse> response = gymFacade.getTrainerTrainings(request);
        return ResponseEntity.ok(response);
    }
}
