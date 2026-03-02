package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class TraineeDaoImpl implements TraineeDao {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Trainee trainee) {
        entityManager.persist(trainee);
        log.info("Saved trainee with id={}, username={}", trainee.getId(), trainee.getUsername());
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Trainee trainee = entityManager.find(Trainee.class, id);
        log.info("Finding trainee by id={}, found: {}", id, trainee != null);
        return Optional.ofNullable(trainee);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            Trainee trainee = entityManager.createQuery("SELECT t FROM Trainee t WHERE t.username = :username"
                    , Trainee.class).setParameter("username", username).getSingleResult();

            log.debug("Found trainee by username={}", username);
            return Optional.ofNullable(trainee);
        } catch (NoResultException e) {
            log.debug("Could NOT find trainee by username={}", username);

        }
        return null;
    }

    @Override
    public void delete(String username) {
        Trainee trainee = findByUsername(username).orElse(null);
        if (trainee != null) {
            entityManager.remove(trainee);
            log.info("Deleted trainee with username={}", username);
        }
    }

    @Override
    public void update(Trainee trainee) {
        entityManager.merge(trainee);
        log.debug("Updated trainee with id={}, username={}", trainee.getId(), trainee.getUsername());
    }

    @Override
    public boolean exists(String username) {
        boolean exists = findByUsername(username) != null;
        log.debug("Checking if trainee exists with username={}, exists: {}", username, exists);
        return exists;
    }
}

