package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.entity.User;
import com.gym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserDao userDao;

    @Override
    public boolean authenticate(String username, String password) {
        log.debug("Authenticating User with username={}", username);
        User user = userDao.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        if (user.getPassword().equals(password) && user.getIsActive()) {
            log.info("Successfully authenticated user with username={}", username);
            return true;
        }
        log.warn("Authentication failed for user username={}: incorrect password or inactive user", username);
        return false;

    }
}
