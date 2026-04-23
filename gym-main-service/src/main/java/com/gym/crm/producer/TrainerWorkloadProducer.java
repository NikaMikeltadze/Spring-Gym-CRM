package com.gym.crm.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.client.WorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadProducer {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${activemq.queue.workload-update}")
    private String workloadQueue;

    public void sendWorkloadUpdate(WorkloadRequest request) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Error serializing workload request: {}", request, e);
            throw new RuntimeException(e);
        }
        log.debug("Sending workload update: {}", payload);
        jmsTemplate.convertAndSend(workloadQueue, payload);
    }
}
