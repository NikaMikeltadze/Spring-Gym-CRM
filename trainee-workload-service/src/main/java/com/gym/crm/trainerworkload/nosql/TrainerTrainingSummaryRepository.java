package com.gym.crm.trainerworkload.nosql;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummary, String>,
                                                         TrainerTrainingSummaryRepositoryCustom {
    Optional<TrainerTrainingSummary> findByUsername(String username);

    List<TrainerTrainingSummary> findByFirstNameAndLastName(String firstName, String lastName);
}
