package com.gym.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "Trainee Id")
    Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "Trainer Id")
    Trainer trainer;

    @Column(name = "Training Name", nullable = false)
    String trainingName;

    @ManyToOne
    @JoinColumn(name = "Training Type Id")
    TrainingType trainingType;

    @Column(name = "Training Date", nullable = false)
    LocalDate trainingDate;

    @Column(name = "Training Duration", nullable = false)
    Double trainingDuration;

}

