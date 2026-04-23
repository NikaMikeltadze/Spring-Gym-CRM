package com.gym.crm.controller;

import com.gym.crm.exception.GlobalExceptionHandler;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

abstract class RestAssuredControllerTestSupport {

    protected void configureMockMvc(Object... controllers) {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controllers)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @AfterEach
    void resetRestAssured() {
        RestAssuredMockMvc.reset();
    }
}
