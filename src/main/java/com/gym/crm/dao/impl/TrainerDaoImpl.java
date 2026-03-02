package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TrainerDaoImpl implements TrainerDao {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Trainer trainer) {
        entityManager.persist(trainer);
        log.debug("Saved trainer with id={}, username={}", trainer.getId(), trainer.getUsername());
    }

    @Override
    public Trainer findByUsername(String username) {
        try {
            Trainer trainer = entityManager.createQuery("SELECT Trainer t FROM Trainer WHERE username = :username", Trainer.class)
                    .setParameter("username", username).getSingleResult();
            log.debug("Finding trainer by username={}, found: {}", username, trainer != null);
            return trainer;
        } catch (NoResultException e) {
            log.debug("Could NOT find trainer by username={}", username);
        }
        return null;
    }

    @Override
    public Trainer findById(Long id) {
        Trainer trainer = entityManager.find(Trainer.class, id);
        log.debug("Finding trainer by id={}, found: {}", id, trainer != null);
        return trainer;
    }

    @Override
    public void update(Trainer trainer) {
        entityManager.merge(trainer);
        log.info("Updated trainer with id={}, username={}", trainer.getId(), trainer.getUsername());
    }

    @Override
    public boolean exists(String username) {
        boolean exists = findByUsername(username) != null;
        log.debug("Checking if trainer exists with username={}, exists: {}", username, exists);
        return exists;
    }
}

