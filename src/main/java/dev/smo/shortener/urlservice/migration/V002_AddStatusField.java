package dev.smo.shortener.urlservice.migration;

import dev.smo.shortener.urlservice.model.UrlStatus;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@ChangeUnit(id = "add-status-field", order = "002", author = "admin")
@RequiredArgsConstructor
public class V002_AddStatusField {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void migrate() {
        var result = mongoTemplate.getCollection("urls")
                .updateMany(
                        new Document("status", new Document("$exists", false)),
                        new Document("$set", new Document("status", UrlStatus.ACTIVE.toString()))
                );

        log.info("Migration completed: {}. Modified documents: {}",
                this.getClass().getName(),
                result.getModifiedCount());
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getCollection("urls")
                .updateMany(
                        new Document("status", "active"),
                        new Document("$unset", new Document("status", ""))
                );
    }
}