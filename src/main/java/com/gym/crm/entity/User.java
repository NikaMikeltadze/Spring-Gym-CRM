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
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First Name must not be blank")
    String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last Name must not be blank")
    String lastName;

    @Column(name = "username", nullable = false, unique = true)
    @NotBlank(message = "Username must not be blank")
    String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password must not be blank")
    String password;

    @Column(name = "is_active", nullable = false)
    @NotNull(message = "IsActive must not be null")
    Boolean isActive;

}

