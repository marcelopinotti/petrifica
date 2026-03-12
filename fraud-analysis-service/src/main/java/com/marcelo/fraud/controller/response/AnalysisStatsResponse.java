package com.marcelo.fraud.controller.response;

public record AnalysisStatsResponse(
        Long totalApproved,
        Long totalRejected,
        Double averageRiskScore
) {}
