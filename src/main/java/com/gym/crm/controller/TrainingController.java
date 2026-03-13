package com.gym.crm.controller;

import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.facade.GymFacade;
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
public class TrainingController {
    private final GymFacade gymFacade;

    @PostMapping("/add")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest request) {
        log.info("Add training request received for traineeUsername={} and trainerUsername={}", request.getTraineeUsername(), request.getTrainerUsername());
        gymFacade.createTraining(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/types")
    public ResponseEntity<GetTrainingTypesResponse> getTrainingTypes() {
        log.info("Get training types request received");
        GetTrainingTypesResponse response = gymFacade.getAllTrainings();
        return ResponseEntity.ok(response);
    }
}
