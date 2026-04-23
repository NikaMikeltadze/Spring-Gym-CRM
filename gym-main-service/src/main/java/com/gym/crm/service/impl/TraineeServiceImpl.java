package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.trainee.*;
import com.gym.crm.dto.request.trainer.GetTrainerTrainingsRequest;
import com.gym.crm.dto.response.trainee.*;
import com.gym.crm.dto.response.trainer.GetTrainerTrainingsResponse;
import com.gym.crm.dto.response.trainer.TrainerProfileInfo;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.mapper.UserMapper;
import com.gym.crm.producer.TrainerWorkloadProducer;
import com.gym.crm.service.TraineeService;
import com.gym.crm.util.UsernamePasswordGenerator;
import com.gym.crm.client.WorkloadRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
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
    private final UserDao userDao;
    private final UsernamePasswordGenerator usernamePasswordGenerator;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final TrainingMapper trainingMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TrainerWorkloadProducer workloadProducer;

    @Override
    @Transactional
    public RegisterTraineeResponse createTrainee(Trainee trainee) {

        log.debug("Creating trainee with firstName={}, lastName={}", trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        String username = usernamePasswordGenerator.generateUsername(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                traineeDao::exists
        );
        if (trainerDao.exists(username)) {
            throw new IllegalStateException("User already registered as trainer: " + username);
        }
        String password = usernamePasswordGenerator.generatePassword();

        trainee.getUser().setUsername(username);
        trainee.getUser().setPassword(passwordEncoder.encode(password));
        trainee.getUser().setIsActive(true);

        traineeDao.save(trainee);
        log.info("Successfully created trainee with username={}", username);
        return new RegisterTraineeResponse(username, password, null);
    }

    @Override
    @Transactional
    public UpdateTraineeProfileResponse updateTrainee(UpdateTraineeProfileRequest request) {
        log.debug("Updating trainee with username={}", request.getUsername());
        Trainee trainee = traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + request.getUsername()));

        traineeMapper.updateEntityFromRequest(request, trainee);
        traineeDao.save(trainee);
        log.info("Successfully updated trainee with username={}", trainee.getUser().getUsername());
        return traineeMapper.toUpdateProfileResponse(trainee);
    }

    @Override
    @Transactional
    public void deleteTrainee(String username) {
        log.debug("Deleting trainee with username={}", username);
        
        List<Training> trainings = trainingDao.findByTraineeUsernameAndCriteria(username, null, null, null, null);
        for (Training training : trainings) {
            try {
                Trainer trainer = training.getTrainer();
                WorkloadRequest request = WorkloadRequest.builder()
                        .trainerUsername(trainer.getUser().getUsername())
                        .trainerFirstName(trainer.getUser().getFirstName())
                        .trainerLastName(trainer.getUser().getLastName())
                        .isActive(trainer.getUser().getIsActive())
                        .trainingDate(java.sql.Date.valueOf(training.getTrainingDate()))
                        .trainingDuration(training.getTrainingDuration())
                        .actionType(WorkloadRequest.ActionType.DELETE)
                        .build();
                log.info("Sending DELETE workload request to trainer-workload queue for trainer: {}", request.getTrainerUsername());
                workloadProducer.sendWorkloadUpdate(request);
            } catch (Exception e) {
                log.error("Failed to call trainer-workload producer for training: {}", training.getId(), e);
            }
        }
        
        traineeDao.delete(username);
        log.info("Successfully deleted trainee with username={}", username);
    }

    @Override
    public Optional<GetTraineeProfileResponse> selectTraineeByUsername(String username) {
        log.debug("Selecting trainee by username={}", username);
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + username));
        log.debug("Found trainee {}", true);
        return Optional.ofNullable(traineeMapper.toGetProfileResponse(trainee));
    }

    @Override
    public Optional<GetTraineeProfileResponse> selectTraineeById(Long id) {
        log.debug("Selecting trainee by id={}", id);
        Trainee trainee = traineeDao.findById(id)
                .orElseThrow(() -> new com.gym.crm.exception.NotFoundException("Trainee not found with id: " + id));
        log.debug("Found trainee {}", true);
        return Optional.ofNullable(traineeMapper.toGetProfileResponse(trainee));
    }

    @Transactional
    @Override
    public void changePassword(@Valid @NotNull ChangeLoginRequest request) {
        log.debug("Attempting to change password for trainee username={}", request.getUsername());

        Trainee trainee = traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", request.getUsername());
                    return new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + request.getUsername());
                });

        if (!passwordEncoder.matches(request.getPassword(), trainee.getUser().getPassword())) {
            log.warn("Password change failed for trainee={}: old password does not match", request.getUsername());
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (request.getPassword().equals(request.getNewPassword())) {
            log.warn("Password change failed for trainee={}: new password is same as old password", request.getUsername());
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        trainee.getUser().setPassword(passwordEncoder.encode(request.getNewPassword()));
        traineeDao.update(trainee);
        log.info("Successfully changed password for trainee username={}", request.getUsername());
    }

    @Transactional
    @Override
    public void activateTrainee(@Valid @NotBlank ActivateTraineeRequest request) {
        log.debug("Attempting to activate trainee username={}", request.getUsername());

        Trainee trainee = traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", request.getUsername());
                    return new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + request.getUsername());
                });

        if (trainee.getUser().getIsActive()) {
            log.warn("Trainee activation failed: trainee={} is already active", request.getUsername());
            throw new IllegalStateException("Trainee is already active");
        }

        trainee.getUser().setIsActive(true);
        traineeDao.update(trainee);
        log.info("Successfully activated trainee username={}", request.getUsername());
    }

    @Override
    @Transactional
    public void deactivateTrainee(@Valid @NotBlank DeactivateTraineeRequest request) {
        log.debug("Attempting to deactivate trainee username={}", request.getUsername());

        Trainee trainee = traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", request.getUsername());
                    return new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + request.getUsername());
                });

        if (!trainee.getUser().getIsActive()) {
            log.warn("Trainee deactivation failed: trainee={} is already inactive", request.getUsername());
            throw new IllegalStateException("Trainee is already inactive");
        }

        trainee.getUser().setIsActive(false);
        traineeDao.update(trainee);
        log.info("Successfully deactivated trainee username={}", request.getUsername());
    }

    @Override
    public List<GetTraineeTrainingsResponse> getTrainings(GetTraineeTrainingsRequest request) {
        log.debug("Fetching trainings for trainee={} with criteria: from={}, to={}, trainer={}, type={}",
                request.getUsername(), request.getStartDate(), request.getEndDate(), request.getTrainerName(), request.getTrainingTypeName());

        traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found with username={}", request.getUsername());
                    return new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + request.getUsername());
                });

        List<Training> trainings = trainingDao.findByTraineeUsernameAndCriteria(request.getUsername(), request.getStartDate(),
                request.getEndDate(), request.getTrainerName(), request.getTrainingTypeName());

        List<GetTraineeTrainingsResponse> result = trainings.stream()
                .map(trainingMapper::toGetTraineeTrainingsResponse)
                .toList();

        log.info("Successfully fetched {} trainings for trainee={}", result.size(), request.getUsername());
        return result;
    }

    @Override
    public UpdateTraineeTrainerListResponse updateTrainerList(UpdateTraineeTrainerListRequest request) {
        Trainee trainee = traineeDao.findByUsername(request.getTraineeUsername()).orElseThrow(() ->
                new com.gym.crm.exception.NotFoundException("Trainee not found with username: " + request.getTraineeUsername()));
        log.debug("Updating trainers for trainee={}", trainee.getUser().getUsername());
        List<Trainer> trainers = new ArrayList<>();

        for (String trainerUsername : request.getTrainerUsernameList()) {
            Trainer trainer = trainerDao.findByUsername(trainerUsername).orElseThrow(() ->
                    new com.gym.crm.exception.NotFoundException("Trainer not found with username: " + trainerUsername));
            trainers.add(trainer);
        }

        trainee.setTrainers(trainers);
        traineeDao.update(trainee);
        log.info("Successfully updated trainers for trainee={}", trainee.getUser().getUsername());
        List<TrainerProfileInfo> trainerList = trainers
                .stream()
                .map(trainerMapper::toProfileInfo)
                .toList();

        return UpdateTraineeTrainerListResponse.builder()
                .trainerList(trainerList)
                .build();
    }

    @Override
    public List<TrainerProfileInfo> getUnassignedActiveTrainers(TraineeAssignableTrainerRequest request) {
        String traineeUsername = request.getUsername();
        return trainerDao.findActiveTrainersNotAssignedTo(traineeUsername)
                .stream()
                .map(trainerMapper::toProfileInfo)
                .toList();
    }

    @Override
    public List<GetTrainerTrainingsResponse> getTrainerTrainings(GetTrainerTrainingsRequest request) {
        trainerDao.findByUsername(request.getUsername())
                .orElseThrow(() -> new com.gym.crm.exception.NotFoundException("Trainer not found with username: " + request.getUsername()));

        return trainingDao
                .findByTrainerUsernameAndCriteria(
                        request.getUsername(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getTraineeName()
                )
                .stream()
                .map(trainerMapper::toGetTrainingsResponse)
                .toList();
    }
}
