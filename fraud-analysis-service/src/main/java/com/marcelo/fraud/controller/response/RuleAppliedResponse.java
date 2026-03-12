package com.marcelo.fraud.controller.response;

public record RuleAppliedResponse(
        String ruleName,
        Boolean passed,
        String message,
        Integer riskScoreImpact)
{}
