package com.marcelo.loan.repository;

import com.marcelo.loan.entity.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanRepository extends MongoRepository<Loan, String> {
}
