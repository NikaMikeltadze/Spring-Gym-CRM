package com.gym.crm.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class AccountLockedException extends RuntimeException {
    private final Instant lockedUntil;

    public AccountLockedException(Instant lockedUntil) {
        super("Account locked until " + lockedUntil);
        this.lockedUntil = lockedUntil;
    }

}
