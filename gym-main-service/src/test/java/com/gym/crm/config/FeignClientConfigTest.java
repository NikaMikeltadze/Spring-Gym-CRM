package com.gym.crm.config;

import com.gym.crm.config.logging.TransactionIdFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeignClientConfigTest {

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
        MDC.clear();
    }

    @Test
    void requestInterceptor_ForwardsAuthorizationAndTransactionId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        MDC.put(TransactionIdFilter.TRANSACTION_ID_KEY, "tx-abc");

        FeignClientConfig config = new FeignClientConfig();
        RequestInterceptor interceptor = config.requestInterceptor();
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertEquals("Bearer token-123", template.headers().get("Authorization").iterator().next());
        assertEquals("tx-abc", template.headers().get(TransactionIdFilter.TRANSACTION_ID_HEADER).iterator().next());
    }

    @Test
    void requestInterceptor_GeneratesTransactionIdWhenMissingInMdc() {
        FeignClientConfig config = new FeignClientConfig();
        RequestInterceptor interceptor = config.requestInterceptor();
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertNotNull(template.headers().get(TransactionIdFilter.TRANSACTION_ID_HEADER));
        assertTrue(template.headers().get(TransactionIdFilter.TRANSACTION_ID_HEADER).iterator().hasNext());
    }
}
