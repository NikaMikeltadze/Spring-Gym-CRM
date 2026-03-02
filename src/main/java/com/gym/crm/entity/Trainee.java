package com.gym.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Trainee extends User {
    @Column(name = "Date of Birth")
    LocalDate dateOfBirth;

    @Column(name = "Address")
    @NotBlank(message = "Address must not be blank")
    String address;

    @OneToOne
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    @NotNull(message = "User must not be null")
    User user;

    @ManyToMany
    @JoinTable(name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<Trainer> trainers = new ArrayList<>();


}

