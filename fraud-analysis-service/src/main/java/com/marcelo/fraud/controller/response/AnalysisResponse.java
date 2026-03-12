package com.marcelo.fraud.controller.response;

import com.marcelo.fraud.entity.enums.Verdict;

import java.math.BigDecimal;
import java.time.Instant;

public record AnalysisResponse(
        String id,
        String loanId,
        String customerId,
        Integer riskScore,
        Verdict verdict,
        String rejectionReason,
        Instant analyzedAt
) {}
