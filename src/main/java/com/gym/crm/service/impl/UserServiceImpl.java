package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.entity.User;
import com.gym.crm.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Transactional
    @Override
    public void changePassword(@Valid @NotNull ChangeLoginRequest request) {
        log.debug("Attempting to change password for user username={}", request.getUsername());

        User user = userDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", request.getUsername());
                    return new IllegalArgumentException("Trainee not found with username: " + request.getUsername());
                });

        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Password change failed for user={}: old password does not match", request.getUsername());
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (request.getPassword().equals(request.getNewPassword())) {
            log.warn("Password change failed for user={}: new password is same as old password", request.getUsername());
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        user.setPassword(request.getNewPassword());
        userDao.save(user);
        log.info("Successfully changed password for user username={}", request.getUsername());
    }
}
