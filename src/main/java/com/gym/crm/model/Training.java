package com.gym.crm.model;

import java.util.Objects;

public class Training {
    Long id;
    Long trainerId;
    Long traineeId;
    String trainingName;
    TrainingType trainingType;

    public Training(Long id, Long trainerId, Long traineeId, String trainingName, TrainingType trainingType) {
        this.id = id;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
    }

    public Training() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training training = (Training) o;

        if (!Objects.equals(id, training.id)) return false;
        if (!Objects.equals(trainerId, training.trainerId)) return false;
        if (!Objects.equals(traineeId, training.traineeId)) return false;
        if (!Objects.equals(trainingName, training.trainingName))
            return false;
        return Objects.equals(trainingType, training.trainingType);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (trainerId != null ? trainerId.hashCode() : 0);
        result = 31 * result + (traineeId != null ? traineeId.hashCode() : 0);
        result = 31 * result + (trainingName != null ? trainingName.hashCode() : 0);
        result = 31 * result + (trainingType != null ? trainingType.hashCode() : 0);
        return result;
    }
}

