package com.marcelo.loan.entity.enums;

public enum LoanEvent {
    SUBMIT,    // PENDING → UNDER_ANALYSIS
    APPROVE,   // UNDER_ANALYSIS → APPROVED
    REJECT,    // UNDER_ANALYSIS → REJECTED
    CANCEL

}
