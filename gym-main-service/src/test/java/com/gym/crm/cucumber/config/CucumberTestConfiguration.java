package com.gym.crm.cucumber.config;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.web.util.DefaultUriBuilderFactory;

@TestConfiguration
public class CucumberTestConfiguration {

    @Bean
    @Lazy
    public TestRestTemplate testRestTemplate(Environment environment) {
        int port = Integer.parseInt(environment.getRequiredProperty("local.server.port"));
        TestRestTemplate template = new TestRestTemplate();
        template.getRestTemplate().setUriTemplateHandler(
                new DefaultUriBuilderFactory("http://localhost:" + port)
        );
        return template;
    }
}
