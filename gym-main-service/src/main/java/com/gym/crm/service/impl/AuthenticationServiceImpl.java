package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.entity.User;
import com.gym.crm.exception.AccountLockedException;
import com.gym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_DURATION_MINUTES = 5;

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Transactional
    @Override
    public boolean authenticate(String username, String password) {
        log.debug("Authenticating User with username={}", username);
        Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("Authentication failed for user username={}: user not found", username);
            return false;
        }

        User currentUser = user.get();
        Instant now = Instant.now(clock);
        if (isLocked(currentUser, now)) {
            log.warn("Authentication blocked for user username={} until {}", username, currentUser.getLockedUntil());
            throw new AccountLockedException(currentUser.getLockedUntil());
        }

        if (passwordEncoder.matches(password, currentUser.getPassword()) && currentUser.getIsActive()) {
            resetLoginAttempts(username);
            log.info("Successfully authenticated user with username={}", username);
            return true;
        }

        registerFailedLoginAttempt(username);
        log.warn("Authentication failed for user username={}: incorrect password or inactive user", username);
        return false;

    }

    @Transactional
    @Override
    public void registerFailedLoginAttempt(String username) {
        userDao.findByUsername(username).ifPresent(user -> {
            registerFailedAttempt(user, Instant.now(clock));
            userDao.save(user);
        });
    }

    @Transactional
    @Override
    public void resetLoginAttempts(String username) {
        userDao.findByUsername(username).ifPresent(user -> {
            resetFailedAuthenticationState(user);
            userDao.save(user);
        });
    }

    private boolean isLocked(User user, Instant now) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(now);
    }

    private void registerFailedAttempt(User user, Instant now) {
        int failedAttempts = Optional.ofNullable(user.getFailedLoginAttempts()).orElse(0) + 1;
        user.setFailedLoginAttempts(failedAttempts);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(now.plusSeconds(LOCK_DURATION_MINUTES * 60));
            user.setFailedLoginAttempts(0);
        }
    }

    private void resetFailedAuthenticationState(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
    }
}
