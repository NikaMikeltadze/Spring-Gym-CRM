package com.gym.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Builder
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "trainee_id")
    @NotNull(message = "Trainee must not be null")
    Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    @NotNull(message = "Trainer must not be null")
    Trainer trainer;

    @Column(name = "training_name", nullable = false)
    @NotBlank(message = "Training Name must not be blank")
    String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    @NotNull(message = "Training Type must not be null")
    TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    @NotNull(message = "Training Date must not be null")
    LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    @NotNull(message = "Training Duration must not be null")
    Double trainingDuration;

}

