package com.marcelo.fraud.entity;

import com.marcelo.fraud.entity.enums.FraudRuleStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fraud_rule_results")
public class FraudRuleResult {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fraud_analysis_id", nullable = false)
    private FraudAnalysis fraudAnalysis;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    private FraudRuleStatus result;

    private String message;

    @Column(name = "created_at")
    private Instant createdAt;
}
