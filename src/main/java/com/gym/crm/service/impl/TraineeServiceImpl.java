package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.util.UsernamePasswordGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class TraineeServiceImpl implements TraineeService {
    //TODO: Transactional
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;

    public void createTrainee(@Valid @NotNull Trainee trainee) {
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

    public void updateTrainee(@Valid @NotNull Trainee trainee) {
        log.debug("Updating trainee with id={}", trainee.getId());
        traineeDao.update(trainee);
        log.info("Successfully updated trainee with username={}", trainee.getUsername());
    }

    public void deleteTrainee(@NotBlank String username) {
        log.debug("Deleting trainee with username={}", username);
        traineeDao.delete(username);
        log.info("Successfully deleted trainee with username={}", username);
    }

    public Optional<Trainee> selectTraineeByUsername(@NotBlank String username) {
        log.debug("Selecting trainee by username={}", username);
        Trainee trainee = traineeDao.findByUsername(username);
        log.debug("Found trainee {}", trainee != null);
        return Optional.ofNullable(trainee);
    }

    public Optional<Trainee> selectTraineeById(@NotNull Long id) {
        log.debug("Selecting trainee by id={}", id);
        Trainee trainee = traineeDao.findById(id);
        log.debug("Found trainee {}", trainee != null);
        return Optional.ofNullable(trainee);
    }

}

