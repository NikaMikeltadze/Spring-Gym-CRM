package com.gym.crm.trainerworkload.nosql;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_training_summary")
@CompoundIndex(def = "{'firstName': 1, 'lastName': 1}", name = "firstLast_idx")
public class TrainerTrainingSummary {

    @Id
    @NotBlank
    private String username;

    @Indexed
    @NotBlank
    private String firstName;

    @Indexed
    @NotBlank
    private String lastName;

    private boolean status;

    private List<YearSummary> years = new ArrayList<>();
}
