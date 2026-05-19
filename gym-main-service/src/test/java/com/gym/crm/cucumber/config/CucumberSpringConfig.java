package com.gym.crm.cucumber.config;

import com.gym.crm.client.TrainerWorkloadClient;
import com.gym.crm.producer.TrainerWorkloadProducer;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(CucumberTestConfiguration.class)
public class CucumberSpringConfig {

    @MockitoBean
    TrainerWorkloadClient trainerWorkloadClient;

    @MockitoBean
    TrainerWorkloadProducer trainerWorkloadProducer;

}
