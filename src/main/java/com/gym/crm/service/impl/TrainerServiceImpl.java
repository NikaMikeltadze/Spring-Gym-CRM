package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.response.trainer.RegisterTrainerResponse;
import com.gym.crm.dto.response.training.GetTrainingTypesResponse;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.mapper.TrainingTypeMapper;
import com.gym.crm.service.TrainerService;
import com.gym.crm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    @Transactional
    public RegisterTrainerResponse createTrainer(Trainer trainer) {
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
        return new RegisterTrainerResponse(username, password);
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
        Optional<Trainer> trainer = trainerDao.findById(id);
        log.debug("Found(by id) trainer: {}", trainer.isPresent());
        return trainer;
    }

    public Optional<Trainer> selectTrainerByUsername(String username) {
        log.debug("Selecting trainer by username={}", username);
        Optional<Trainer> trainer = trainerDao.findByUsername(username);
        log.debug("Found(by username) trainer: {}", trainer.isPresent());
        return trainer;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Attempting to change password for trainer username={}", username);

        Optional<Trainer> trainer = trainerDao.findByUsername(username);
        if (trainer.isEmpty()) {
            throw new IllegalArgumentException("Trainer not found with username: " + username);
        }

        if (!trainer.get().getPassword().equals(oldPassword)) {
            log.warn("Password change failed for trainer={}: old password does not match", username);
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (oldPassword.equals(newPassword)) {
            log.warn("Password change failed for trainer={}: new password is same as old password", username);
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        trainer.get().setPassword(newPassword);
        trainerDao.update(trainer.get());
        log.info("Successfully changed password for trainer username={}", username);
    }

    @Override
    @Transactional
    public void activateTrainer(String username) {
        log.debug("Attempting to activate trainer username={}", username);

        Optional<Trainer> trainer = trainerDao.findByUsername(username);
        if (trainer.isEmpty()) {
            throw new IllegalArgumentException("Trainer not found with username: " + username);
        }

        if (trainer.get().getIsActive()) {
            log.warn("Trainer activation failed: trainer={} is already active", username);
            throw new IllegalStateException("Trainer is already active");
        }

        trainer.get().setIsActive(true);
        trainerDao.update(trainer.get());
        log.info("Successfully activated trainer username={}", username);
    }

    @Override
    @Transactional
    public void deactivateTrainer(String username) {
        log.debug("Attempting to deactivate trainer username={}", username);

        Optional<Trainer> trainer = trainerDao.findByUsername(username);
        if (trainer.isEmpty()) {
            throw new IllegalArgumentException("Trainer not found with username: " + username);
        }

        if (!trainer.get().getIsActive()) {
            log.warn("Trainer deactivation failed: trainer={} is already inactive", username);
            throw new IllegalStateException("Trainer is already inactive");
        }

        trainer.get().setIsActive(false);
        trainerDao.update(trainer.get());
        log.info("Successfully deactivated trainer username={}", username);
    }

    @Override
    public GetTrainingTypesResponse getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        log.debug("Fetching trainings for trainer={} with criteria: from={}, to={}, trainee={}",
                username, fromDate, toDate, traineeName);

        Optional.ofNullable(trainerDao.findByUsername(username))
                .orElseThrow(() -> {
                    log.error("Trainer not found with username={}", username);
                    return new IllegalArgumentException("Trainer not found with username: " + username);
                });

        List<Training> trainings = trainingDao.findByTrainerUsernameAndCriteria(username, fromDate, toDate, traineeName);
        List<TrainingTypeInfo> result = trainings.stream()
                .map(training -> trainingTypeMapper.toTrainingTypeInfo(training.getTrainingType()))
                .toList();

        log.info("Successfully fetched {} trainings for trainer={}", result.size(), username);
        return new GetTrainingTypesResponse(result);
    }
}
