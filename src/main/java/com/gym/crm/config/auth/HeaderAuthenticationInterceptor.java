package com.gym.crm.config.auth;

import com.gym.crm.exception.UnauthorizedException;
import com.gym.crm.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeaderAuthenticationInterceptor implements HandlerInterceptor {

    public static final String USERNAME_HEADER = "X-Username";
    public static final String PASSWORD_HEADER = "X-Password";
    public static final String MDC_USERNAME_KEY = "username";

    private final AuthenticationService authenticationService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String username = request.getHeader(USERNAME_HEADER);
        String password = request.getHeader(PASSWORD_HEADER);

        if (isBlank(username) || isBlank(password)) {
            throw new UnauthorizedException("Missing authentication headers");
        }

        try {
            if (!authenticationService.authenticate(username, password)) {
                throw new UnauthorizedException("Invalid username or password");
            }
        } catch (IllegalArgumentException ex) {
            log.debug("Authentication failed due to service validation error", ex);
            throw new UnauthorizedException("Invalid username or password");
        }

        MDC.put(MDC_USERNAME_KEY, username);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        MDC.remove(MDC_USERNAME_KEY);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
