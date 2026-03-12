package com.marcelo.fraud.entity;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleApplied {
    private String ruleName;
    private Boolean passed;
    private String message;
    private Integer riskScoreImpact;
    private Instant appliedAt;
}
