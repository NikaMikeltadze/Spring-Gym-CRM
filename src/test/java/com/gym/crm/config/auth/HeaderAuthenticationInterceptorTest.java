package com.gym.crm.config.auth;

import com.gym.crm.exception.UnauthorizedException;
import com.gym.crm.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderAuthenticationInterceptorTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final Object handler = new Object();

    @AfterEach
    void cleanMdc() {
        MDC.clear();
    }

    @Test
    void preHandle_AllowsRequest_WhenCredentialsAreValid() {
        HeaderAuthenticationInterceptor interceptor = new HeaderAuthenticationInterceptor(authenticationService);
        when(request.getHeader(HeaderAuthenticationInterceptor.USERNAME_HEADER)).thenReturn("john.doe");
        when(request.getHeader(HeaderAuthenticationInterceptor.PASSWORD_HEADER)).thenReturn("securePass1");
        when(authenticationService.authenticate("john.doe", "securePass1")).thenReturn(true);

        boolean allowed = interceptor.preHandle(request, response, handler);

        assertTrue(allowed);
        assertEquals("john.doe", MDC.get(HeaderAuthenticationInterceptor.MDC_USERNAME_KEY));
        verify(authenticationService).authenticate("john.doe", "securePass1");
    }

    @Test
    void preHandle_RejectsRequest_WhenHeadersAreMissing() {
        HeaderAuthenticationInterceptor interceptor = new HeaderAuthenticationInterceptor(authenticationService);
        when(request.getHeader(HeaderAuthenticationInterceptor.USERNAME_HEADER)).thenReturn(null);
        when(request.getHeader(HeaderAuthenticationInterceptor.PASSWORD_HEADER)).thenReturn("securePass1");

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> interceptor.preHandle(request, response, handler));

        assertEquals("Missing authentication headers", ex.getMessage());
        verify(authenticationService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void preHandle_RejectsRequest_WhenCredentialsAreInvalid() {
        HeaderAuthenticationInterceptor interceptor = new HeaderAuthenticationInterceptor(authenticationService);
        when(request.getHeader(HeaderAuthenticationInterceptor.USERNAME_HEADER)).thenReturn("john.doe");
        when(request.getHeader(HeaderAuthenticationInterceptor.PASSWORD_HEADER)).thenReturn("wrongPass");
        when(authenticationService.authenticate("john.doe", "wrongPass")).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> interceptor.preHandle(request, response, handler));

        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void preHandle_RejectsRequest_WhenAuthServiceThrowsIllegalArgumentException() {
        HeaderAuthenticationInterceptor interceptor = new HeaderAuthenticationInterceptor(authenticationService);
        when(request.getHeader(HeaderAuthenticationInterceptor.USERNAME_HEADER)).thenReturn("john.doe");
        when(request.getHeader(HeaderAuthenticationInterceptor.PASSWORD_HEADER)).thenReturn("securePass1");
        when(authenticationService.authenticate("john.doe", "securePass1"))
                .thenThrow(new IllegalArgumentException("User not found"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> interceptor.preHandle(request, response, handler));

        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void afterCompletion_ClearsUsernameFromMdc() {
        HeaderAuthenticationInterceptor interceptor = new HeaderAuthenticationInterceptor(authenticationService);
        MDC.put(HeaderAuthenticationInterceptor.MDC_USERNAME_KEY, "john.doe");

        interceptor.afterCompletion(request, response, handler, null);

        assertNull(MDC.get(HeaderAuthenticationInterceptor.MDC_USERNAME_KEY));
    }
}
