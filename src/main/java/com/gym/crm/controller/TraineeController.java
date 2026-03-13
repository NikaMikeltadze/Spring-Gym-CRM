package com.gym.crm.controller;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.response.trainee.*;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.facade.GymFacade;
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
public class TraineeController {
    private final GymFacade gymFacade;
    private final TraineeDao traineeDao;

    @PostMapping("/register")
    public ResponseEntity<RegisterTraineeResponse> registerTrainee(@Valid @RequestBody RegisterTraineeRequest traineeRequest) {
        RegisterTraineeResponse response = gymFacade.createTrainee(traineeRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<GetTraineeProfileResponse> getTraineeProfile(@PathVariable String username) {
        GetTraineeProfileResponse response = gymFacade.getTraineeByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UpdateTraineeProfileResponse> updateTraineeProfile(@Valid @RequestBody UpdateTraineeProfileRequest request) {
        UpdateTraineeProfileResponse response = gymFacade.updateTrainee(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteTraineeProfile(@Valid @RequestBody DeleteTraineeProfileRequest request) {
        gymFacade.deleteTrainee(request.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate")
    public ResponseEntity<Void> activateTrainee(@Valid @RequestBody ActivateTraineeRequest request) {
        gymFacade.activateTrainee(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deactivate/{username}")
    public ResponseEntity<Void> deactivateTrainee(@PathVariable String username, @RequestParam Boolean isActive) {
        gymFacade.deactivateTrainee(new DeactivateTraineeRequest(username, isActive));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainings/{username}")
    public ResponseEntity<List<GetTraineeTrainingsResponse>> getTraineeTrainings(@PathVariable String username,
                                                                                 @Valid @ModelAttribute GetTraineeTrainingsRequest request) {
        request.setUsername(username);
        List<GetTraineeTrainingsResponse> response = gymFacade.getTraineeTrainings(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/trainers")
    public ResponseEntity<UpdateTraineeTrainerListResponse> updateTraineeTrainerList(@Valid @RequestBody UpdateTraineeTrainerListRequest request) {
        UpdateTraineeTrainerListResponse response = gymFacade.updateTrainerList(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned-trainers/{username}")
    public ResponseEntity<List<TrainerProfileInfo>> getUnassignedActiveTrainers(@PathVariable String username) {
        List<TrainerProfileInfo> response = gymFacade.getUnassignedActiveTrainers(username);
        return ResponseEntity.ok(response);
    }
}
