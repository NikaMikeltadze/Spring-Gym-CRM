package com.gym.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "First Name", nullable = false)
    @NotBlank(message = "First Name must not be blank")
    String firstName;

    @Column(name = "Last Name", nullable = false)
    @NotBlank(message = "Last Name must not be blank")
    String lastName;

    @Column(name = "Username", nullable = false, unique = true)
    @NotBlank(message = "Username must not be blank")
    String username;

    @Column(name = "Password", nullable = false)
    @NotBlank(message = "Password must not be blank")
    String password;

    @Column(name = "IsActive", nullable = false)
    @NotNull(message = "IsActive must not be null")
    Boolean isActive;

}

