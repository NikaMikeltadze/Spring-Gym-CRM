package com.gym.crm.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionIdFilterTest {

    @Test
    void doFilter_ReusesIncomingTransactionId() throws ServletException, IOException {
        TransactionIdFilter filter = new TransactionIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(TransactionIdFilter.TRANSACTION_ID_HEADER, "tx-incoming-1");

        AtomicReference<String> transactionIdInsideChain = new AtomicReference<>();
        FilterChain chain = (req, res) ->
                transactionIdInsideChain.set(MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY));

        filter.doFilter(request, response, chain);

        assertEquals("tx-incoming-1", transactionIdInsideChain.get());
        assertEquals("tx-incoming-1", response.getHeader(TransactionIdFilter.TRANSACTION_ID_HEADER));
        assertNull(MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY));
    }

    @Test
    void doFilter_GeneratesTransactionIdWhenMissing() throws ServletException, IOException {
        TransactionIdFilter filter = new TransactionIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> transactionIdInsideChain = new AtomicReference<>();
        FilterChain chain = (req, res) ->
                transactionIdInsideChain.set(MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY));

        filter.doFilter(request, response, chain);

        String generated = transactionIdInsideChain.get();
        assertFalse(generated == null || generated.isBlank());
        assertEquals(generated, response.getHeader(TransactionIdFilter.TRANSACTION_ID_HEADER));
        assertNull(MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY));
    }
}
