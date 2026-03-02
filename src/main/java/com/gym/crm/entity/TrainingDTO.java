package com.gym.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class TrainingDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "Trainee Id")
    @NotNull(message = "Trainee must not be null")
    TraineeDTO trainee;

    @ManyToOne
    @JoinColumn(name = "Trainer Id")
    @NotNull(message = "Trainer must not be null")
    TrainerDTO trainer;

    @Column(name = "Training Name", nullable = false)
    @NotBlank(message = "Training Name must not be blank")
    String trainingName;

    @ManyToOne
    @JoinColumn(name = "Training Type Id")
    @NotNull(message = "Training Type must not be null")
    TrainingType trainingType;

    @Column(name = "Training Date", nullable = false)
    @NotNull(message = "Training Date must not be null")
    LocalDate trainingDate;

    @Column(name = "Training Duration", nullable = false)
    @NotNull(message = "Training Duration must not be null")
    Double trainingDuration;

}

