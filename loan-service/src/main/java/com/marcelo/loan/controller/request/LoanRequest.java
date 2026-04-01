package com.marcelo.loan.controller.request;

import com.marcelo.loan.entity.enums.LoanReason;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanRequest(
        @NotNull @Positive BigDecimal requestedAmount,
        @NotNull @Min(1) Integer installments,
        @NotNull LoanReason reason
) {}
