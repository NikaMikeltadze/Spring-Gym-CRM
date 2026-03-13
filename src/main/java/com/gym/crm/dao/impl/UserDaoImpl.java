package com.gym.crm.dao.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository

public class UserDaoImpl implements UserDao {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> user = entityManager.createQuery("SELECT user FROM User user WHERE user.username = :username"
                        , User.class).setParameter("username", username)
                .getResultList();
        log.debug("Found user by username={}", username);
        return user.stream().findFirst();

    }
    @Override
    public void save(User user) {
        entityManager.persist(user);
        log.info("Saved user with id={}, username={}", user.getId(), user.getUsername());
    }
}
