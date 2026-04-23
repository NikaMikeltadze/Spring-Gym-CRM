package com.gym.crm.dao;

import com.gym.crm.entity.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);
    void save(User user);
}
