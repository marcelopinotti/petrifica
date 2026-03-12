package com.marcelo.fraud.repository;

import com.marcelo.fraud.entity.Analysis;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends MongoRepository<Analysis, String> {
    Optional<Analysis> findByLoanId(String loanId);
    List<Analysis> findByCustomerId(String customerId);
}
