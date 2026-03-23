package com.gym.crm.config;

import com.gym.crm.config.logging.TransactionIdFilter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilterRegistration() {
        FilterRegistrationBean<TransactionIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TransactionIdFilter());
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(
                DispatcherType.REQUEST,
                DispatcherType.FORWARD,
                DispatcherType.INCLUDE,
                DispatcherType.ASYNC,
                DispatcherType.ERROR
        );
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "gym-crm");
    }

}

