package com.marcelo.loan.event;

public record FraudAnalysisResultEvent(
        String loanId,
        String customerId,
        String verdict,
        String rejectionReason
) {
}

