package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.TrainingDTO;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Training;
import com.gym.crm.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;
    private final ModelMapper modelMapper;

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
        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + username));
        log.debug("Found trainee {}", trainee != null);
        return Optional.ofNullable(trainee);
    }

    public Optional<Trainee> selectTraineeById(Long id) {
        log.debug("Selecting trainee by id={}", id);
        Trainee trainee = traineeDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Trainee not found with id: " + id));
        log.debug("Found trainee {}", trainee != null);
        return Optional.ofNullable(trainee);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Attempting to change password for trainee username={}", username);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", username);
                    return new IllegalArgumentException("Trainee not found with username: " + username);
                });

        if (!trainee.getPassword().equals(oldPassword)) {
            log.warn("Password change failed for trainee={}: old password does not match", username);
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (oldPassword.equals(newPassword)) {
            log.warn("Password change failed for trainee={}: new password is same as old password", username);
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        trainee.setPassword(newPassword);
        traineeDao.update(trainee);
        log.info("Successfully changed password for trainee username={}", username);
    }

    @Override
    @Transactional
    public void activateTrainee(String username) {
        log.debug("Attempting to activate trainee username={}", username);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", username);
                    return new IllegalArgumentException("Trainee not found with username: " + username);
                });

        if (trainee.getIsActive()) {
            log.warn("Trainee activation failed: trainee={} is already active", username);
            throw new IllegalStateException("Trainee is already active");
        }

        trainee.setIsActive(true);
        traineeDao.update(trainee);
        log.info("Successfully activated trainee username={}", username);
    }

    @Override
    @Transactional
    public void deactivateTrainee(String username) {
        log.debug("Attempting to deactivate trainee username={}", username);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", username);
                    return new IllegalArgumentException("Trainee not found with username: " + username);
                });

        if (!trainee.getIsActive()) {
            log.warn("Trainee deactivation failed: trainee={} is already inactive", username);
            throw new IllegalStateException("Trainee is already inactive");
        }

        trainee.setIsActive(false);
        traineeDao.update(trainee);
        log.info("Successfully deactivated trainee username={}", username);
    }

    @Override
    public List<TrainingDTO> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        log.debug("Fetching trainings for trainee={} with criteria: from={}, to={}, trainer={}, type={}",
                username, fromDate, toDate, trainerName, trainingTypeName);

        traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", username);
                    return new IllegalArgumentException("Trainee not found with username: " + username);
                });

        List<Training> trainings = trainingDao.findByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
        List<TrainingDTO> result = trainings.stream()
                .map(training -> modelMapper.map(training, TrainingDTO.class))
                .toList();

        log.info("Successfully fetched {} trainings for trainee={}", result.size(), username);
        return result;
    }
}

