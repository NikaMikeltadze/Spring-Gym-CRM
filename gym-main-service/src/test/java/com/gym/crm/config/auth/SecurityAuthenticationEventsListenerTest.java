package com.gym.crm.config.auth;

import com.gym.crm.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecurityAuthenticationEventsListenerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Test
    void onAuthenticationFailure_RegistersFailedAttempt_WhenUsernameIsPresent() {
        SecurityAuthenticationEventsListener listener = new SecurityAuthenticationEventsListener(authenticationService);
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated("john.doe", "bad-pass");
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(
                authentication,
                new org.springframework.security.authentication.BadCredentialsException("Bad credentials")
        );

        listener.onAuthenticationFailure(event);

        verify(authenticationService).registerFailedLoginAttempt("john.doe");
    }

    @Test
    void onAuthenticationSuccess_ResetsAttempts_WhenUsernameIsPresent() {
        SecurityAuthenticationEventsListener listener = new SecurityAuthenticationEventsListener(authenticationService);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated("john.doe", "pass", java.util.List.of());
        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        listener.onAuthenticationSuccess(event);

        verify(authenticationService).resetLoginAttempts("john.doe");
    }

    @Test
    void onAuthenticationFailure_DoesNothing_WhenUsernameIsBlank() {
        SecurityAuthenticationEventsListener listener = new SecurityAuthenticationEventsListener(authenticationService);
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated("   ", "bad-pass");
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(
                authentication,
                new org.springframework.security.authentication.BadCredentialsException("Bad credentials")
        );

        listener.onAuthenticationFailure(event);

        verify(authenticationService, never()).registerFailedLoginAttempt("   ");
    }
}
