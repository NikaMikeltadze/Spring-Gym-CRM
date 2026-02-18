package com.gym.crm.config;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Map;

@Configuration
@ComponentScan("com.gym.crm")
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean
    public Map<Long, Trainer> trainerStorage() {
        return new java.util.HashMap<>();
    }

    @Bean
    public Map<Long, Trainee> traineeStorage() {
        return new java.util.HashMap<>();
    }

    @Bean
    public Map<Long, Training> trainingStorage() {
        return new java.util.HashMap<>();
    }

    @Bean
    public Map<Long, TrainingType> trainingTypeStorage() {
        return new java.util.HashMap<>();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

