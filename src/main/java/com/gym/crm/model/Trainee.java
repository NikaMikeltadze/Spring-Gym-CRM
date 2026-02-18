package com.gym.crm.model;

import java.util.Objects;

public class Trainee extends User {
    String dateOfBirth;
    String address;
    Long userId;

    public Trainee(String dateOfBirth, String address, Long userId) {
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userId = userId;
    }

    public Trainee() {
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

        Trainee trainee = (Trainee) o;

        if (!Objects.equals(dateOfBirth, trainee.dateOfBirth)) return false;
        if (!Objects.equals(address, trainee.address)) return false;
        return Objects.equals(userId, trainee.userId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}

