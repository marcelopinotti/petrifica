package com.marcelo.fraud.repository;

import com.marcelo.fraud.entity.Analysis;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository {
    Optional<Analysis> findByLoanId(String loanId);
    List<Analysis> findByCustomerId(String customerId);
}
