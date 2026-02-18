package com.gym.crm.storage;

import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.dao.impl.TrainerDaoImpl;
import com.gym.crm.dao.impl.TrainingDaoImpl;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StorageInitializerTest {

    private StorageInitializer storageInitializer;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Training> trainingStorage;
    private Map<Long, TrainingType> trainingTypeStorage;
    private TraineeDaoImpl traineeDaoImpl;
    private TrainerDaoImpl trainerDaoImpl;
    private TrainingDaoImpl trainingDaoImpl;

    @BeforeEach
    void setUp() {
        storageInitializer = new StorageInitializer();
        trainerStorage = new HashMap<>();
        traineeStorage = new HashMap<>();
        trainingStorage = new HashMap<>();
        trainingTypeStorage = new HashMap<>();

        traineeDaoImpl = new TraineeDaoImpl(traineeStorage);
        trainerDaoImpl = new TrainerDaoImpl(trainerStorage);
        trainingDaoImpl = new TrainingDaoImpl(trainingStorage);

        storageInitializer.setTrainerStorage(trainerStorage);
        storageInitializer.setTraineeStorage(traineeStorage);
        storageInitializer.setTrainingStorage(trainingStorage);
        storageInitializer.setTrainingTypeStorage(trainingTypeStorage);
        storageInitializer.setTraineeDaoImpl(traineeDaoImpl);
        storageInitializer.setTrainerDaoImpl(trainerDaoImpl);
        storageInitializer.setTrainingDaoImpl(trainingDaoImpl);

        storageInitializer.setFileName("storage-data.txt");
    }

    @Test
    void initData_LoadsBaseUserFields() {
        storageInitializer.initData();

        assertFalse(trainerStorage.isEmpty(), "Trainer storage should not be empty");
        Trainer johnSmith = trainerStorage.values().stream()
                .filter(t -> "John.Smith".equals(t.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(johnSmith, "Trainer John.Smith should be loaded");
        assertEquals("John", johnSmith.getFirstName(), "First name should be John");
        assertEquals("Smith", johnSmith.getLastName(), "Last name should be Smith");
        assertEquals("aB3dEfGh1K", johnSmith.getPassword(), "Password should be loaded");
        assertTrue(johnSmith.isActive(), "Active status should be true");

        assertFalse(traineeStorage.isEmpty(), "Trainee storage should not be empty");
        Trainee sarahWilliams = traineeStorage.values().stream()
                .filter(t -> "Sarah.Williams".equals(t.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(sarahWilliams, "Trainee Sarah.Williams should be loaded");
        assertEquals("Sarah", sarahWilliams.getFirstName(), "First name should be Sarah");
        assertEquals("Williams", sarahWilliams.getLastName(), "Last name should be Williams");
        assertEquals("qF5nBrM3gZ", sarahWilliams.getPassword(), "Password should be loaded");
        assertTrue(sarahWilliams.isActive(), "Active status should be true");
    }
}

