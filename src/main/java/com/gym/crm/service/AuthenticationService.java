package com.gym.crm.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface AuthenticationService {
    boolean authenticate(@Valid @NotNull String username, @Valid @NotNull String password);
}
