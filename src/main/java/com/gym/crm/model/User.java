package com.gym.crm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Long id;
    String firstName;
    String lastName;
    String username;
    String password;
    boolean isActive;

}

