package com.marcelo.loan.event;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanRequestedEvent(
        String loanId,
        String customerId,
        String customerCpf,
        BigDecimal customerMonthlyIncome,
        BigDecimal requestedAmount,
        Integer installments,
        String reason,
        Instant requestedAt
) {
}
