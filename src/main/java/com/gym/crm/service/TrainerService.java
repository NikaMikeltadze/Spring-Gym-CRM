package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.util.UsernamePasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrainerService {

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private UsernamePasswordGenerator usernamePasswordGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUsernamePasswordGenerator(UsernamePasswordGenerator usernamePasswordGenerator) {
        this.usernamePasswordGenerator = usernamePasswordGenerator;
    }

    public void createTrainer(Trainer trainer) {
        log.debug("Creating trainer with firstName={}, lastName={}", trainer != null ? trainer.getFirstName() : null, trainer != null ? trainer.getLastName() : null);

        if (trainer == null) {
            log.error("Attempted to create null trainer");
            throw new IllegalArgumentException("Trainer must not be null");
        }
        if (isBlank(trainer.getFirstName())) {
            log.error("Attempted to create trainer with blank first name");
            throw new IllegalArgumentException("First name must not be blank");
        }
        if (isBlank(trainer.getLastName())) {
            log.error("Attempted to create trainer with blank last name");
            throw new IllegalArgumentException("Last name must not be blank");
        }

        // Generate username and password
        String username = usernamePasswordGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                u -> traineeDao.exists(u) || trainerDao.exists(u)
        );
        String password = usernamePasswordGenerator.generatePassword();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        trainerDao.save(trainer);
        log.info("Successfully created trainer with username={}", username);
    }

    public void updateTrainer(Trainer trainer) {
        log.debug("Updating trainer with id={}", trainer != null ? trainer.getId() : null);

        if (trainer == null || trainer.getId() == null) {
            log.error("Attempted to update trainer with null trainer or id");
            throw new IllegalArgumentException("Trainer and trainer.id must not be null");
        }
        if (isBlank(trainer.getUsername())) {
            log.error("Attempted to update trainer with blank username");
            throw new IllegalArgumentException("Username must not be blank");
        }

        trainerDao.update(trainer);
        log.info("Successfully updated trainer with username={}", trainer.getUsername());
    }

    public Trainer selectTrainerById(Long id) {
        log.debug("Selecting trainer by id={}", id);

        if (id == null) {
            log.error("Attempted to select trainer with null id");
            throw new IllegalArgumentException("Id must not be null");
        }

        Trainer trainer = trainerDao.findById(id);
        log.debug("Found trainer: {}", trainer != null);
        return trainer;
    }

    public Trainer selectTrainerByUsername(String username) {
        log.debug("Selecting trainer by username={}", username);

        if (isBlank(username)) {
            log.error("Attempted to select trainer with blank username");
            throw new IllegalArgumentException("Username must not be blank");
        }

        Trainer trainer = trainerDao.findByUsername(username);
        log.debug("Found trainer: {}", trainer != null);
        return trainer;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

