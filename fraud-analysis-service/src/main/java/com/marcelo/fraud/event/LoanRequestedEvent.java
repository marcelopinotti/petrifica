package com.marcelo.fraud.event;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanRequestedEvent(
        String loanId,
        String customerId,
        BigDecimal requestedAmount,
        BigDecimal declaredIncome
) {}
