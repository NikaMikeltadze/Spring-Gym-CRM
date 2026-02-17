package com.gym.crm.storage;

import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.dao.impl.TrainerDaoImpl;
import com.gym.crm.dao.impl.TrainingDaoImpl;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class StorageInitializer {

    @Value("${storage.data.file}")
    private String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Training> trainingStorage;
    private Map<Long, TrainingType> trainingTypeStorage;
    private TraineeDaoImpl traineeDaoImpl;
    private TrainerDaoImpl trainerDaoImpl;
    private TrainingDaoImpl trainingDaoImpl;

    @Autowired
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setTrainingTypeStorage(Map<Long, TrainingType> trainingTypeStorage) {
        this.trainingTypeStorage = trainingTypeStorage;
    }

    @PostConstruct
    public void initData() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String currentSection = null;
            Map<String, User> userMap = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                switch (line) {
                    case "TRAINING_TYPE":
                    case "USER":
                    case "TRAINER":
                    case "TRAINEE":
                    case "TRAINING":
                        currentSection = line;
                        continue;
                }

                String[] parts = line.split(",");
                switch (Objects.requireNonNull(currentSection)) {
                    case "TRAINING_TYPE":
                        // parts[0] = TrainingTypeName
                        TrainingType trainingType = new TrainingType();
                        trainingType.setId(trainingTypeStorage.size() + 1L);
                        trainingType.setName(parts[0].trim());
                        trainingTypeStorage.put(trainingType.getId(), trainingType);
                        break;
                    case "USER": {
                        // parts[0]=FirstName, [1]=LastName, [2]=Username,
                        // [3]=Password, [4]=IsActive
                        User user = new User();
                        user.setFirstName(parts[0].trim());
                        user.setLastName(parts[1].trim());
                        user.setUsername(parts[2].trim());
                        user.setPassword(parts[3].trim());
                        user.setActive(Boolean.parseBoolean(parts[4].trim()));
                        userMap.put(user.getUsername(), user);
                        break;
                    }
                    case "TRAINER": {
                        // parts[0]=Username, [1]=Specialization
                        String username = parts[0].trim();
                        Trainer trainer = new Trainer();
                        trainer.setSpecialization(parts[1].trim());
                        trainer.setUsername(username);
                        trainer.setId(trainerStorage.size() + 1L);

                        User u = userMap.get(username);
                        if (u != null) {
                            trainer.setFirstName(u.getFirstName());
                            trainer.setLastName(u.getLastName());
                            trainer.setPassword(u.getPassword());
                            trainer.setActive(u.isActive());
                        }

                        trainerStorage.put(trainer.getId(), trainer);
                        break;
                    }
                    case "TRAINEE": {
                        // parts[0]=Username, [1]=DateOfBirth, [2]=Address
                        String username = parts[0].trim();
                        Trainee trainee = new Trainee();
                        trainee.setUsername(username);
                        trainee.setDateOfBirth(parts[1].trim());
                        trainee.setAddress(parts[2].trim());
                        trainee.setId(traineeStorage.size() + 1L);

                        User u = userMap.get(username);
                        if (u != null) {
                            trainee.setFirstName(u.getFirstName());
                            trainee.setLastName(u.getLastName());
                            trainee.setPassword(u.getPassword());
                            trainee.setActive(u.isActive());
                        }

                        traineeStorage.put(trainee.getId(), trainee);
                        break;
                    }
                    case "TRAINING": {
                        // parts[0]=TraineeUsername, [1]=TrainerUsername,
                        // [2]=TrainingName, [3]=TrainingType,
                        // [4]=TrainingDate, [5]=DurationMinutes
                        Training training = new Training();
                        training.setTrainingName(parts[2].trim());
                        training.setTraineeId(traineeDaoImpl.findByUsername(parts[0].trim()).getId());
                        training.setTrainerId(trainerDaoImpl.findByUsername(parts[1].trim()).getId());

                        String typeName = parts[3].trim();
                        TrainingType type = trainingTypeStorage.values()
                                .stream()
                                .filter(tt -> tt.getName().equals(typeName))
                                .findFirst().orElse(null);
                        training.setTrainingType(type);

                        training.setId(trainingStorage.size() + 1L);
                        trainingStorage.put(training.getId(), training);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setTraineeDaoImpl(TraineeDaoImpl traineeDaoImpl) {
        this.traineeDaoImpl = traineeDaoImpl;
    }

    @Autowired
    public void setTrainerDaoImpl(TrainerDaoImpl trainerDaoImpl) {
        this.trainerDaoImpl = trainerDaoImpl;
    }

    @Autowired
    public void setTrainingDaoImpl(TrainingDaoImpl trainingDaoImpl) {
        this.trainingDaoImpl = trainingDaoImpl;
    }
}
