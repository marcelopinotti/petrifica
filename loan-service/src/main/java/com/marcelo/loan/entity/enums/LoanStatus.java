package com.marcelo.loan.entity.enums;

public enum LoanStatus {
    PENDING,
    UNDER_ANALYSIS,
    APPROVED,
    REJECTED,
    CANCELLED;

    public boolean canTransitionTo(LoanStatus next) {
        return switch (this) {
            case PENDING -> next == UNDER_ANALYSIS || next == CANCELLED;
            case UNDER_ANALYSIS -> next == APPROVED || next == REJECTED;
            case APPROVED, REJECTED, CANCELLED -> false;
        };
    }
}
