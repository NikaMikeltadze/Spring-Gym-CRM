package com.gym.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class TraineeDTO extends User {
    @Column(name = "Date of Birth")
    LocalDate dateOfBirth;

    @Column(name = "Address")
    @NotBlank(message = "Address must not be blank")
    String address;

    @OneToOne
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    @NotNull(message = "User must not be null")
    User user;

}

