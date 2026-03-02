package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.service.TrainerService;
import com.gym.crm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final TrainingDao trainingDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void createTrainer(Trainer trainer) {
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

    @Override
    @Transactional
    public void updateTrainer(Trainer trainer) {
        log.debug("Updating trainer with id={}", trainer.getId());
        trainerDao.update(trainer);
        log.info("Successfully updated trainer with username={}", trainer.getUsername());
    }

    public Optional<Trainer> selectTrainerById(Long id) {
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

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Attempting to change password for trainer username={}", username);
        
        Trainer trainer = Optional.ofNullable(trainerDao.findByUsername(username))
                .orElseThrow(() -> {
                    log.error("Trainer not found with username={}", username);
                    return new IllegalArgumentException("Trainer not found with username: " + username);
                });

        if (!trainer.getPassword().equals(oldPassword)) {
            log.warn("Password change failed for trainer={}: old password does not match", username);
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (oldPassword.equals(newPassword)) {
            log.warn("Password change failed for trainer={}: new password is same as old password", username);
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        trainer.setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Successfully changed password for trainer username={}", username);
    }

    @Override
    @Transactional
    public void activateTrainer(String username) {
        log.debug("Attempting to activate trainer username={}", username);
        
        Trainer trainer = Optional.ofNullable(trainerDao.findByUsername(username))
                .orElseThrow(() -> {
                    log.error("Trainer not found with username={}", username);
                    return new IllegalArgumentException("Trainer not found with username: " + username);
                });

        if (trainer.getIsActive()) {
            log.warn("Trainer activation failed: trainer={} is already active", username);
            throw new IllegalStateException("Trainer is already active");
        }

        trainer.setIsActive(true);
        trainerDao.update(trainer);
        log.info("Successfully activated trainer username={}", username);
    }

    @Override
    @Transactional
    public void deactivateTrainer(String username) {
        log.debug("Attempting to deactivate trainer username={}", username);
        
        Trainer trainer = Optional.ofNullable(trainerDao.findByUsername(username))
                .orElseThrow(() -> {
                    log.error("Trainer not found with username={}", username);
                    return new IllegalArgumentException("Trainer not found with username: " + username);
                });

        if (!trainer.getIsActive()) {
            log.warn("Trainer deactivation failed: trainer={} is already inactive", username);
            throw new IllegalStateException("Trainer is already inactive");
        }

        trainer.setIsActive(false);
        trainerDao.update(trainer);
        log.info("Successfully deactivated trainer username={}", username);
    }

    @Override
    public List<TrainingDTO> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        log.debug("Fetching trainings for trainer={} with criteria: from={}, to={}, trainee={}",
                username, fromDate, toDate, traineeName);
        
        Optional.ofNullable(trainerDao.findByUsername(username))
                .orElseThrow(() -> {
                    log.error("Trainer not found with username={}", username);
                    return new IllegalArgumentException("Trainer not found with username: " + username);
                });

        List<Training> trainings = trainingDao.findByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        List<TrainingDTO> result = trainings.stream()
                .map(training -> modelMapper.map(training, TrainingDTO.class))
                .toList();
        
        log.info("Successfully fetched {} trainings for trainer={}", result.size(), username);
        return result;
    }
}
