package com.gym.crm.trainerworkload.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.trainerworkload.model.TrainerWorkloadRequest;
import com.gym.crm.trainerworkload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadConsumer {
    private final TrainerWorkloadService trainerWorkloadService;
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;

    private static final String TRANSACTION_ID_PROPERTY = "x-transaction-id";
    private static final String TRANSACTION_ID_KEY = "transactionId";

    @Value("${activemq.queue.workload-dlq}")
    private String dlqQueue;

    @JmsListener(destination = "${activemq.queue.workload-update}")
    public void processWorkloadMessage(String payload, Message message) {
        String transactionId = extractTransactionId(message);
        MDC.put(TRANSACTION_ID_KEY, transactionId);

        try {
            log.debug("tx={} - Received workload update message", transactionId);
            TrainerWorkloadRequest request = objectMapper.readValue(payload, TrainerWorkloadRequest.class);
            trainerWorkloadService.processWorkload(request);
            log.info("tx={} - Workload message processed successfully", transactionId);
        } catch (JsonProcessingException e) {
            log.error("tx={} - Error deserializing workload request, sending to DLQ: {}", transactionId, payload, e);
            jmsTemplate.convertAndSend(dlqQueue, payload);
        } catch (Exception e) {
            log.error("tx={} - Unexpected error processing workload message, sending to DLQ: {}", transactionId, payload, e);
            jmsTemplate.convertAndSend(dlqQueue, payload);
        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }

    // Extract transactionId from JMS message properties or generate a new one if not present
    private String extractTransactionId(Message message) {
        if (message != null) {
            try {
                String txId = message.getStringProperty(TRANSACTION_ID_PROPERTY);
                if (txId != null && !txId.isBlank()) {
                    return txId;
                }
            } catch (JMSException ignored) {
                // Not Found
            }
        }
        return UUID.randomUUID().toString();
    }
}
