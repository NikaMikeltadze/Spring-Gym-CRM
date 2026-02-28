package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Training training) {
        entityManager.persist(training);
        log.debug("Saved training with id={}, name={}", training.getId(), training.getTrainingName());
    }

    @Override
    public Training findById(Long id) {
        Training training = entityManager.find(Training.class, id);
        log.debug("Finding training by id={}, found: {}", id, training != null);
        return training;
    }

    @Override
    public List<Training> findAll() {
        List<Training> trainings = entityManager.createQuery("SELECT t FROM Training t", Training.class).getResultList();
        log.debug("Finding all trainings, count: {}", trainings.size());
        return trainings;
    }
}

