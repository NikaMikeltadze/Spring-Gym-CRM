package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(TrainingType trainingType) {
        entityManager.persist(trainingType);
        log.info("Saved TrainingType with id={}, name={}", trainingType.getId(), trainingType.getName());
    }

    @Override
    public TrainingType findById(Long id) {
        return entityManager.find(TrainingType.class, id);
    }

    @Override
    public List<TrainingType> findAll() {
        return entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class).getResultList();
    }
}

