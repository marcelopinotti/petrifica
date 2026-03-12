package com.marcelo.loan.controller.request;

import com.marcelo.loan.entity.enums.LoanReason;

import java.math.BigDecimal;

public record LoanRequest(
        BigDecimal requestedAmount,
        Integer installments,
        LoanReason reason
) {}
