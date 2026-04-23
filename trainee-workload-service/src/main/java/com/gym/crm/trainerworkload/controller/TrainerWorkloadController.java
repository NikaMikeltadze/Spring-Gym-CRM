package com.gym.crm.trainerworkload.controller;

import com.gym.crm.trainerworkload.model.TrainerMonthlyWorkloadResponse;
import com.gym.crm.trainerworkload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/workloads")
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;

    public TrainerWorkloadController(TrainerWorkloadService trainerWorkloadService) {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @GetMapping("/{trainerUsername}/workload")
    public ResponseEntity<TrainerMonthlyWorkloadResponse> getTrainerMonthlyWorkload(
            @PathVariable String trainerUsername,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        log.debug("Getting flat monthly workload for trainer: {}, year: {}, month: {}", trainerUsername, year, month);
        return ResponseEntity.ok(trainerWorkloadService.getTrainerMonthlyWorkload(trainerUsername, year, month));
    }
}
