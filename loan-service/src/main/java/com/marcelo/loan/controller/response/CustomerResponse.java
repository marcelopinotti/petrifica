package com.marcelo.loan.controller.response;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomerResponse(
        String id,
        String fullName,
        String email,
        String cpf,
        BigDecimal monthlyIncome,
        Instant createdAt
) {}
