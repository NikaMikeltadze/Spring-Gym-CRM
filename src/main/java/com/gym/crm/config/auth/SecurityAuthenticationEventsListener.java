package com.gym.crm.config.auth;

import com.gym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityAuthenticationEventsListener {

    private final AuthenticationService authenticationService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = resolveUsername(event.getAuthentication());
        if (username == null) {
            return;
        }
        authenticationService.registerFailedLoginAttempt(username);
        log.debug("Registered failed login attempt for username={}", username);
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = resolveUsername(event.getAuthentication());
        if (username == null) {
            return;
        }
        authenticationService.resetLoginAttempts(username);
        log.debug("Reset failed login attempts for username={}", username);
    }

    private String resolveUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        String username = authentication.getName().trim();
        return username.isEmpty() ? null : username;
    }
}
