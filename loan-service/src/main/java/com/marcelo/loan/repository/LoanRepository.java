package com.marcelo.loan.repository;

import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.enums.LoanStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanRepository extends MongoRepository<Loan, String> {
    List<Loan> findByCustomerId(String customerId);
    List<Loan> findByStatus(LoanStatus status);
    boolean existsByCustomerIdAndStatusIn(String customerId, List<LoanStatus> statuses);
}
