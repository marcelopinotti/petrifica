package com.marcelo.fraud.controller.response;

public record RuleAppliedResponse(
        String ruleName,
        String message,
        Integer riskScoreImpact
) {}
