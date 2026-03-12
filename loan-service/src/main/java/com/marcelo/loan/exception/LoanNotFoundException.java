package com.marcelo.loan.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(String loanId) {
        super("Empréstimo com ID " + loanId + " não encontrado");
    }
}
