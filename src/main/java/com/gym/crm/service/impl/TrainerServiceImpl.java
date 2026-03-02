package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import com.gym.crm.util.UsernamePasswordGenerator;
import jakarta.validation.Valid;
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
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;

    public void createTrainer(@Valid @NotNull Trainer trainer) {
        log.debug("Creating trainer with firstName={}, lastName={}", trainer.getFirstName(), trainer.getLastName());

        // Generate username and password
        String username = usernamePasswordGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                user -> traineeDao.exists(user) || trainerDao.exists(user)
        );
        String password = usernamePasswordGenerator.generatePassword();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setIsActive(true);

        trainerDao.save(trainer);
        log.info("Successfully created trainer with username={}", username);
    }

    public void updateTrainer(@Valid @NotNull Trainer trainer) {
        log.debug("Updating trainer with id={}", trainer.getId());
        trainerDao.update(trainer);
        log.info("Successfully updated trainer with username={}", trainer.getUsername());
    }

    public Optional<Trainer> selectTrainerById(@NotNull Long id) {
        log.debug("Selecting trainer by id={}", id);
        Trainer trainer = trainerDao.findById(id);
        log.debug("Found(by id) trainer: {}", trainer != null);
        return Optional.ofNullable(trainer);
    }

    public Optional<Trainer> selectTrainerByUsername(String username) {
        log.debug("Selecting trainer by username={}", username);
        Trainer trainer = trainerDao.findByUsername(username);
        log.debug("Found(by username) trainer: {}", trainer != null);
        return Optional.ofNullable(trainer);
    }

}
