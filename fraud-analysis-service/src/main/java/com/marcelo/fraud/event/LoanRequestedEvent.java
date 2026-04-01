package com.marcelo.fraud.event;

import java.math.BigDecimal;

public record LoanRequestedEvent(
        String loanId,
        String customerId,
        BigDecimal requestedAmount,
        BigDecimal declaredIncome
) {}
