package com.marcelo.fraud.event;

import com.marcelo.fraud.entity.enums.Verdict;

public record FraudAnalysisResultEvent(
        String loanId,
        String customerId,
        Verdict verdict,
        String rejectionReason
) {}
