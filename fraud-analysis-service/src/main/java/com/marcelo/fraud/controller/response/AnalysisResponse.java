package com.marcelo.fraud.controller.response;

import com.marcelo.fraud.entity.enums.Verdict;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AnalysisResponse(
        String id,
        String loanId,
        String customerId,
        Integer riskScore,
        Verdict verdict,
        String rejectionReason,
        List<RuleAppliedResponse> rules,
        Instant analyzedAt
) {}
