package com.marcelo.fraud.repository;

import com.marcelo.fraud.entity.RiskRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface RiskRuleRepository extends MongoRepository<RiskRule, String> {
    List<RiskRule> findByActiveTrue();
}
