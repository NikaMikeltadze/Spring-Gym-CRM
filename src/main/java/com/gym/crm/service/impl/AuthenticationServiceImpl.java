package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.entity.User;
import com.gym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserDao userDao;

    @Override
    public boolean authenticate(String username, String password) {
        log.debug("Authenticating User with username={}", username);
        Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("Authentication failed for user username={}: user not found", username);
            return false;
        }
        if (user.get().getPassword().equals(password) && user.get().getIsActive()) {
            log.info("Successfully authenticated user with username={}", username);
            return true;
        }
        log.warn("Authentication failed for user username={}: incorrect password or inactive user", username);
        return false;

    }
}
