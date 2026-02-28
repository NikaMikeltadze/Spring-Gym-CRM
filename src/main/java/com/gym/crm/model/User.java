package com.gym.crm.model;

import jakarta.persistence.*;
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
    String firstName;

    @Column(name = "Last Name", nullable = false)
    String lastName;

    @Column(name = "Username", nullable = false, unique = true)
    String username;

    @Column(name = "Password", nullable = false)
    String password;

    @Column(name = "IsActive", nullable = false)
    Boolean isActive;

}

