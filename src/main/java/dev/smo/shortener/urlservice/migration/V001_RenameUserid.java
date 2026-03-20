package dev.smo.shortener.urlservice.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@ChangeUnit(id = "rename-userid-to-user", order = "001", author = "admin")
@RequiredArgsConstructor
public class V001_RenameUserid {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void migrate() {
        var result = mongoTemplate.getCollection("urls")
                .updateMany(
                        new Document("userid", new Document("$exists", true)),
                        new Document("$rename", new Document("userid", "user"))
                );

        log.info("Migration completed: {}. Modified documents: {}", this.getClass().getName(), result.getModifiedCount());
    }

    /**
     This method is mandatory even when transactions are enabled.
     They are used in the undo operation and any other scenario where transactions are not an option.
     However, note that when transactions are avialble and Mongock need to rollback, this method is ignored.
     **/
    @RollbackExecution
    public void rollback() {
    }
}