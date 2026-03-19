package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<Trainer> findByUsername(String username) {
        List<Trainer> trainers = entityManager.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultList();
        log.debug("Finding trainer by username={}, found: {}", username, !trainers.isEmpty());

        return trainers.stream().findFirst();
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Trainer trainer = entityManager.find(Trainer.class, id);
        log.debug("Finding trainer by id={}, found: {}", id, trainer != null);
        return Optional.ofNullable(trainer);
    }

    @Override
    public void update(Trainer trainer) {
        entityManager.merge(trainer);
        log.info("Updated trainer with id={}, username={}", trainer.getId(), trainer.getUsername());
    }

    @Override
    public boolean exists(String username) {
        boolean exists = findByUsername(username).isPresent();
        log.debug("Checking if trainer exists with username={}, exists: {}", username, exists);
        return exists;
    }

    @Override
    public List<Trainer> findActiveTrainersNotAssignedTo(String traineeUsername) {
        List<Trainer> trainers = entityManager.createQuery(
                        "SELECT tr FROM Trainer tr " +
                                "WHERE tr.isActive = true " +
                                "AND tr.id NOT IN (" +
                                "    SELECT assignedTrainees.id FROM Trainee t JOIN t.trainers assignedTrainees " +
                                "    WHERE t.username = :traineeUsername" +
                                ")",
                        Trainer.class
                )
                .setParameter("traineeUsername", traineeUsername)
                .getResultList();

        log.debug("Found {} active trainers not assigned to trainee with username={}", trainers.size(), traineeUsername);
        return trainers;
    }
}
