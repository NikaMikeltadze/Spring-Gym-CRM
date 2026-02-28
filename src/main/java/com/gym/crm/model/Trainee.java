package com.gym.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    String address;

    @OneToOne
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    User user;

}

