package com.gym.crm.service;

import com.gym.crm.dao.UserDao;
import com.gym.crm.entity.User;
import com.gym.crm.exception.AccountLockedException;
import com.gym.crm.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Clock clock;
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-03-31T10:00:00Z"), ZoneOffset.UTC);
        authenticationService = new AuthenticationServiceImpl(userDao, passwordEncoder, clock);
    }

    @Test
    void authenticate_ReturnsTrueAndResetsState_WhenCredentialsAreValid() {
        User user = createUser();
        user.setFailedLoginAttempts(2);
        user.setLockedUntil(Instant.parse("2026-03-31T09:59:00Z"));

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);

        boolean authenticated = authenticationService.authenticate("john.doe", "pass");

        assertTrue(authenticated);
        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockedUntil());
        verify(userDao).save(user);
    }

    @Test
    void authenticate_LocksUserForFiveMinutes_AfterThreeFailedAttempts() {
        User user = createUser();
        user.setFailedLoginAttempts(2);

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        boolean authenticated = authenticationService.authenticate("john.doe", "wrong");

        assertFalse(authenticated);
        assertEquals(0, user.getFailedLoginAttempts());
        assertEquals(Instant.parse("2026-03-31T10:05:00Z"), user.getLockedUntil());
        verify(userDao).save(user);
    }

    @Test
    void authenticate_ThrowsAccountLocked_WhenUserIsCurrentlyLocked() {
        User user = createUser();
        user.setLockedUntil(Instant.parse("2026-03-31T10:04:59Z"));

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(user));

        AccountLockedException ex = assertThrows(AccountLockedException.class,
                () -> authenticationService.authenticate("john.doe", "pass"));

        assertEquals("Account locked until 2026-03-31T10:04:59Z", ex.getMessage());
        verify(passwordEncoder, never()).matches("pass", "encoded");
        verify(userDao, never()).save(user);
    }

    private User createUser() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("encoded");
        user.setIsActive(true);
        user.setFailedLoginAttempts(0);
        return user;
    }
}
