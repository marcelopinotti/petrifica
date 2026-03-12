package com.marcelo.fraud.controller.request;

import com.marcelo.fraud.entity.enums.Verdict;

import java.time.Instant;

public record AnalysisFilterRequest(
        String customerId,
        Verdict verdict,
        Instant from,
        Instant to
) {}