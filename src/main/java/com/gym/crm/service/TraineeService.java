package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.util.UsernamePasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TraineeService {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private UsernamePasswordGenerator usernamePasswordGenerator;


    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUsernamePasswordGenerator(UsernamePasswordGenerator usernamePasswordGenerator) {
        this.usernamePasswordGenerator = usernamePasswordGenerator;
    }


    public void createTrainee(Trainee trainee) {
        log.debug("Creating trainee with firstName={}, lastName={}", trainee != null ? trainee.getFirstName() : null, trainee != null ? trainee.getLastName() : null);

        if (trainee == null) {
            log.error("Attempted to create null trainee");
            throw new IllegalArgumentException("Trainee must not be null");
        }
        if (isBlank(trainee.getFirstName())) {
            log.error("Attempted to create trainee with blank first name");
            throw new IllegalArgumentException("First name must not be blank");
        }
        if (isBlank(trainee.getLastName())) {
            log.error("Attempted to create trainee with blank last name");
            throw new IllegalArgumentException("Last name must not be blank");
        }

        // Generate username and password
        String username = usernamePasswordGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                user -> traineeDao.exists(user) || traineeDao.exists(user)
        );
        String password = usernamePasswordGenerator.generatePassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        traineeDao.save(trainee);
        log.info("Successfully created trainee with username={}", username);
    }

    public void updateTrainee(Trainee trainee) {
        log.debug("Updating trainee with id={}", trainee != null ? trainee.getId() : null);

        if (trainee == null || trainee.getId() == null) {
            log.error("Attempted to update trainee with null trainee or id");
            throw new IllegalArgumentException("Trainee and trainee.id must not be null");
        }
        if (isBlank(trainee.getUsername())) {
            log.error("Attempted to update trainee with blank username");
            throw new IllegalArgumentException("Username must not be blank");
        }

        traineeDao.update(trainee);
        log.info("Successfully updated trainee with username={}", trainee.getUsername());
    }

    public void deleteTrainee(String username) {
        log.debug("Deleting trainee with username={}", username);

        if (isBlank(username)) {
            log.error("Attempted to delete trainee with blank username");
            throw new IllegalArgumentException("Username must not be blank");
        }

        traineeDao.delete(username);
        log.info("Successfully deleted trainee with username={}", username);
    }

    public Trainee selectTraineeByUsername(String username) {
        log.debug("Selecting trainee by username={}", username);

        if (isBlank(username)) {
            log.error("Attempted to select trainee with blank username");
            throw new IllegalArgumentException("Username must not be blank");
        }

        Trainee trainee = traineeDao.findByUsername(username);
        log.debug("Found trainee: {}", trainee != null);
        return trainee;
    }

    public Trainee selectTraineeById(Long id) {
        log.debug("Selecting trainee by id={}", id);

        if (id == null) {
            log.error("Attempted to select trainee with null id");
            throw new IllegalArgumentException("Id must not be null");
        }

        Trainee trainee = traineeDao.findById(id);
        log.debug("Found trainee: {}", trainee != null);
        return trainee;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

