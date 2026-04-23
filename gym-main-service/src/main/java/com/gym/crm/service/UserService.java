package com.gym.crm.service;

import com.gym.crm.dto.request.ChangeLoginRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface UserService {
    void changePassword(@Valid @NotNull ChangeLoginRequest request);
}
