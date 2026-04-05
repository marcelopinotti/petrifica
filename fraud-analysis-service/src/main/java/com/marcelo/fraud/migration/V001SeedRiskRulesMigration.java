package com.marcelo.fraud.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Seed inicial de regras de risco para o fraud-analysis-service.
 */
@ChangeUnit(id = "fraud-risk-rules-001", order = "001", author = "marcelo")
public class V001SeedRiskRulesMigration {

    private static final String COLLECTION = "risk_rules";

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        if (mongoTemplate.count(new Query(), COLLECTION) > 0) {
            return;
        }

        List<Document> rules = List.of(
                buildRule("R1_AMOUNT_OVER_HALF_INCOME", "Valor > 50% da renda mensal", 30),
                buildRule("R2_AMOUNT_OVER_50000", "Valor solicitado > 50000", 40),
                buildRule("R3_MULTIPLE_ACTIVE_LOANS", "Mais de um emprestimo ativo", 25),
                buildRule("R4_INSTALLMENTS_OVER_48", "Parcelas > 48", 20)
        );

        mongoTemplate.insert(rules, COLLECTION);
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        Query query = Query.query(Criteria.where("name").in(
                "R1_AMOUNT_OVER_HALF_INCOME",
                "R2_AMOUNT_OVER_50000",
                "R3_MULTIPLE_ACTIVE_LOANS",
                "R4_INSTALLMENTS_OVER_48"
        ));
        mongoTemplate.remove(query, COLLECTION);
    }

    private Document buildRule(String name, String description, int weight) {
        Document document = new Document();
        document.put("name", name);
        document.put("description", description);
        document.put("weight", weight);
        document.put("active", true);
        return document;
    }
}

