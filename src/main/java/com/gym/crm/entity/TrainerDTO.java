package com.gym.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class TrainerDTO extends User {
    @ManyToOne
    @JoinColumn(name = "Specialization")
    @NotNull
    TrainingType trainingType;

    @OneToOne
    @JoinColumn(name = "User Id", nullable = false, unique = true)
    @NotNull(message = "User must not be null")
    User user;
}

