package com.gym.crm.trainerworkload.nosql;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainerTrainingSummaryRepositoryCustomImpl implements TrainerTrainingSummaryRepositoryCustom {
    private static final Logger log = LoggerFactory.getLogger(TrainerTrainingSummaryRepositoryCustomImpl.class);

    private final MongoTemplate mongoTemplate;

    // Atomically increment the trainingSummaryDuration for a specific year/month nested within a trainer document.
    @Override
    public boolean incrementMonthlyDuration(double delta, int year, int month) {
        try {
            Document updateDocument = new Document("$set",
                    new Document("years.$[y].months.$[m].trainingSummaryDuration",
                            new Document("$max", Arrays.asList(
                                    0,
                                    new Document("$add", Arrays.asList(
                                            "$years.$[y].months.$[m].trainingSummaryDuration",
                                            delta
                                    ))
                            ))
                    )
            );

            Document yearFilter = new Document("y.year", year);
            Document monthFilter = new Document("m.month", month);

            UpdateResult result = mongoTemplate.getCollection("trainer_training_summary")
                    .updateMany(
                            new Document(),
                            List.of(updateDocument),
                            new com.mongodb.client.model.UpdateOptions()
                                    .arrayFilters(Arrays.asList(yearFilter, monthFilter))
                    );

            log.debug("Atomic increment result: matched={}, modified={}",
                    result.getMatchedCount(), result.getModifiedCount());

            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            log.debug("Atomic increment failed (expected if nested element doesn't exist): {}", e.getMessage());
            return false;
        }
    }
}



