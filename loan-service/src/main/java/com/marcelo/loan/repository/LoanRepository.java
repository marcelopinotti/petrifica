package com.marcelo.loan.repository;

import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.enums.LoanStatus;

import java.util.List;

public interface LoanRepository  {
    List<Loan> findByCustomerId(String customerId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByStatusIn(List<LoanStatus> statuses);
    boolean existsByCustomerIdAndStatusIn(String customerId, List<LoanStatus> statuses);
}
