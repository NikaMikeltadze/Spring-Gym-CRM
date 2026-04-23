package com.gym.crm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "trainer-workload")
public interface TrainerWorkloadClient {

    @GetMapping("/api/workloads/{trainerUsername}/workload")
    @CircuitBreaker(name = "workloadClient", fallbackMethod = "getMonthlyWorkloadFallback")
    WorkloadSummaryResponse getMonthlyWorkload(@PathVariable("trainerUsername") String trainerUsername,
                                               @RequestParam(value = "year", required = false) Integer year,
                                               @RequestParam(value = "month", required = false) Integer month);

    default WorkloadSummaryResponse getMonthlyWorkloadFallback(String trainerUsername, Integer year, Integer month, Throwable t) {
        System.err.println("Workload service is down! Failed to fetch workload for trainer: " + trainerUsername);
        return WorkloadSummaryResponse.builder()
                .trainerUsername(trainerUsername)
                .year(year)
                .month(month)
                .trainingSummaryDuration(0.0)
                .build();
    }
}
