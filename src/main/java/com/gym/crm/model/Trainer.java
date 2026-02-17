package com.gym.crm.model;

import java.util.Objects;

public class Trainer extends User {
    String specialization;
    Long userId;

    public Trainer(String specialization, Long userId) {
        this.specialization = specialization;
        this.userId = userId;
    }

    public Trainer() {
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Trainer trainer = (Trainer) o;

        if (!Objects.equals(specialization, trainer.specialization))
            return false;
        return Objects.equals(userId, trainer.userId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (specialization != null ? specialization.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
