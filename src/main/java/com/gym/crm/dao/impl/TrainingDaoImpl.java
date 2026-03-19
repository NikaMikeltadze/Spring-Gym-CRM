package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<Training> findById(Long id) {
        Training training = entityManager.find(Training.class, id);
        log.debug("Finding training by id={}, found: {}", id, training != null);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> findAll() {
        List<Training> trainings = entityManager.createQuery("SELECT t FROM Training t", Training.class).getResultList();
        log.debug("Finding all trainings, count: {}", trainings.size());
        return trainings;
    }

    @Override
    public List<Training> findByTraineeUsername(String traineeUsername) {
        log.debug("Finding trainings by trainee username={}", traineeUsername);
        List<Training> trainings = entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainee.username = :username",
                        Training.class)
                .setParameter("username", traineeUsername)
                .getResultList();
        log.debug("Found {} trainings", trainings.size());
        return trainings;
    }

    @Override
    public List<Training> findByTrainerUsername(String trainerUsername) {
        log.debug("Finding trainings by trainer username={}", trainerUsername);
        List<Training> trainings = entityManager.createQuery(
                "SELECT t FROM Training t WHERE t.trainer.username = :username",
                Training.class
        ).setParameter("username", trainerUsername).getResultList();
        log.debug("Found {} trainings for trainer={}", trainings.size(), trainerUsername);
        return trainings;
    }

    @Override
    public List<Training> findByDateRange(LocalDate fromDate, LocalDate toDate) {
        log.debug("Finding trainings by date range: from={}, to={}", fromDate, toDate);

        StringBuilder hql = new StringBuilder("SELECT t FROM Training t WHERE 1=1");

        if (fromDate != null) {
            hql.append(" AND t.trainingDate >= :fromDate");
        }
        if (toDate != null) {
            hql.append(" AND t.trainingDate <= :toDate");
        }

        var query = entityManager.createQuery(hql.toString(), Training.class);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }

        List<Training> result = query.getResultList();
        log.debug("Found {} trainings in date range", result.size());
        return result;
    }

    @Override
    public List<Training> findByTraineeId(Long traineeId) {
        log.debug("Finding trainings by trainee id={}", traineeId);
        List<Training> trainings = entityManager.createQuery(
                "SELECT t FROM Training t WHERE t.trainee.id = :traineeId",
                Training.class
        ).setParameter("traineeId", traineeId).getResultList();
        log.debug("Found {} trainings for trainee id={}", trainings.size(), traineeId);
        return trainings;
    }

    @Override
    public List<Training> findByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName) {

        log.debug("Finding trainings for trainee={} with criteria: from={}, to={}, trainer={}, type={}",
                traineeUsername, fromDate, toDate, trainerName, trainingTypeName);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> root = cq.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("trainee").get("username"), traineeUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }

        if (trainerName != null && !trainerName.isEmpty()) {
            predicates.add(cb.like(
                    cb.concat(
                            cb.concat(root.get("trainer").get("firstName"), cb.literal(" ")),
                            root.get("trainer").get("lastName")
                    ),
                    "%" + trainerName + "%"
            ));
        }

        if (trainingTypeName != null && !trainingTypeName.isEmpty()) {
            predicates.add(cb.equal(root.get("trainingType").get("name"), trainingTypeName));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Training> result = entityManager.createQuery(cq).getResultList();
        log.debug("Found {} trainings matching criteria", result.size());
        return result;
    }

    @Override
    public List<Training> findByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName) {

        log.debug("Finding trainings for trainer={} with criteria: from={}, to={}, trainee={}",
                trainerUsername, fromDate, toDate, traineeName);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> root = cq.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("trainer").get("username"), trainerUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }

        if (traineeName != null && !traineeName.isEmpty()) {
            predicates.add(cb.like(
                    cb.concat(
                            cb.concat(root.get("trainee").get("firstName"), cb.literal(" ")),
                            root.get("trainee").get("lastName")
                    ),
                    "%" + traineeName + "%"
            ));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Training> result = entityManager.createQuery(cq).getResultList();
        log.debug("Found {} trainings matching criteria", result.size());
        return result;
    }
}

