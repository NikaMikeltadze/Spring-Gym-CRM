package com.gym.crm.trainerworkload.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import com.gym.crm.trainerworkload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadConsumer {
    private final TrainerWorkloadService trainerWorkloadService;
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;

    @Value("${activemq.queue.workload-dlq}")
    private String dlqQueue;

    @JmsListener(destination = "${activemq.queue.workload-update}")
    public void processWorkloadMessage(String payload) {
        log.debug("Received workload update message: {}", payload);
        try {
            TrainerWorkloadRequest request = objectMapper.readValue(payload, TrainerWorkloadRequest.class);
            trainerWorkloadService.processWorkload(request);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing workload request, sending to DLQ: {}", payload, e);
            jmsTemplate.convertAndSend(dlqQueue, payload);
        } catch (Exception e) {
            log.error("Unexpected error processing workload message, sending to DLQ: {}", payload, e);
            jmsTemplate.convertAndSend(dlqQueue, payload);
        }
    }
}
