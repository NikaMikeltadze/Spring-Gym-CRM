package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class TraineeServiceImpl implements TraineeService {
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;

    @Override
    @Transactional
    public void createTrainee(Trainee trainee) {
        log.debug("Creating trainee with firstName={}, lastName={}", trainee.getFirstName(), trainee.getLastName());

        // Generate username and password
        String username = usernamePasswordGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                user -> traineeDao.exists(user) || trainerDao.exists(user)
        );
        String password = usernamePasswordGenerator.generatePassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setIsActive(true);

        traineeDao.save(trainee);
        log.info("Successfully created trainee with username={}", username);
    }

    @Override
    @Transactional
    public void updateTrainee(Trainee trainee) {
        log.debug("Updating trainee with id={}", trainee.getId());
        traineeDao.update(trainee);
        log.info("Successfully updated trainee with username={}", trainee.getUsername());
    }

    @Override
    @Transactional
    public void deleteTrainee(String username) {
        log.debug("Deleting trainee with username={}", username);
        traineeDao.delete(username);
        log.info("Successfully deleted trainee with username={}", username);
    }

    public Optional<Trainee> selectTraineeByUsername(String username) {
        log.debug("Selecting trainee by username={}", username);
        Trainee trainee = traineeDao.findByUsername(username).orElse(null);
        log.debug("Found trainee {}", trainee != null);
        return Optional.ofNullable(trainee);
    }

    public Optional<Trainee> selectTraineeById(Long id) {
        log.debug("Selecting trainee by id={}", id);
        Trainee trainee = traineeDao.findById(id).orElse(null);
        log.debug("Found trainee {}", trainee != null);
        return Optional.ofNullable(trainee);
    }

}

