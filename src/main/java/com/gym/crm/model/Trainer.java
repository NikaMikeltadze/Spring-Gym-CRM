package com.gym.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Trainer extends User {
    @ManyToOne
    @JoinColumn(name = "Specialization")
    @NotNull
    TrainingType trainingType;

    @OneToOne
    @JoinColumn(name = "User Id", nullable = false, unique = true)
    @NotNull(message = "User must not be null")
    User user;
}

