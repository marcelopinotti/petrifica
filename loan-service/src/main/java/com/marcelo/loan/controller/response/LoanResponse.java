package com.marcelo.loan.controller.response;

import com.marcelo.loan.entity.enums.LoanReason;
import com.marcelo.loan.entity.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
        String id,
        String customerId,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        Integer installments,
        BigDecimal interestRate,
        LoanReason reason,
        LoanStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
