package com.gym.crm.config;

import com.gym.crm.config.logging.TransactionIdFilter;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Forward Auth token
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }

            // Add Transaction ID
            String transactionId = MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY);
            if (transactionId == null) {
                transactionId = UUID.randomUUID().toString();
            }
            requestTemplate.header(TransactionIdFilter.TRANSACTION_ID_HEADER, transactionId);
        };
    }
}
