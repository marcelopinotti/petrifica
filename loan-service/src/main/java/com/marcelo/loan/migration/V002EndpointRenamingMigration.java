package com.marcelo.loan.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;

/**
 * Migration para registrar a atualização dos nomes dos endpoints no metadado do sistema.
 */
@ChangeUnit(id = "endpoint-renaming-002", order = "002", author = "marcelo")
public class V002EndpointRenamingMigration {

    private static final String COLLECTION = "app_metadata";
    private static final String KEY = "endpoint-version";

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        Query query = Query.query(Criteria.where("key").is(KEY));
        
        Document metadata = new Document();
        metadata.put("key", KEY);
        metadata.put("description", "Troca de nome de endpoints para melhor padronização");
        metadata.put("version", "2.0");
        metadata.put("updatedAt", Instant.now());

        Update update = new Update();
        metadata.forEach(update::set);
        mongoTemplate.upsert(query, update, COLLECTION);
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        Query query = Query.query(Criteria.where("key").is(KEY));
        mongoTemplate.remove(query, COLLECTION);
    }
}
