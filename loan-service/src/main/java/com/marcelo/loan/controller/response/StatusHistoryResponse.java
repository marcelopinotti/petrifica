package com.marcelo.loan.controller.response;

import com.marcelo.loan.entity.enums.LoanStatus;

import java.time.Instant;

public record StatusHistoryResponse(
        LoanStatus status,
        Instant changedAt,
        String notes
) {}