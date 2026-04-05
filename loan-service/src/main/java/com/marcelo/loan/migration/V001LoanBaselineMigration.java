package com.marcelo.loan.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Migration baseline para registrar metadado inicial do loan-service.
 */
@ChangeUnit(id = "loan-baseline-001", order = "001", author = "marcelo")
public class V001LoanBaselineMigration {

    private static final String COLLECTION = "app_metadata";
    private static final String KEY = "loan-service-baseline";

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        Query query = Query.query(Criteria.where("key").is(KEY));
        if (mongoTemplate.exists(query, COLLECTION)) {
            return;
        }

        Document metadata = new Document();
        metadata.put("key", KEY);
        metadata.put("description", "Baseline migration applied by Mongock");
        metadata.put("version", 1);

        mongoTemplate.insert(metadata, COLLECTION);
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        Query query = Query.query(Criteria.where("key").is(KEY));
        mongoTemplate.remove(query, COLLECTION);
    }
}

