package com.marcelo.fraud.entity;

import com.marcelo.fraud.entity.enums.FraudVerdict;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fraud_analysis")
public class FraudAnalysis {

    @Id
    private UUID id;

   // @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false, unique = true)
    private UUID loanId;

   // @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "total_rules")
    private Integer totalRules;

    @Column(name = "passed_rules")
    private Integer passedRules;

    @Column(name = "failed_rules")
    private Integer failedRules;

    @Column(name = "inconclusive_rules")
    private Integer inconclusiveRules;

    @Column(name = "pass_percentage")
    private BigDecimal passPercentage;

    @Enumerated(EnumType.STRING)
    private FraudVerdict verdict;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    private String notes;

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @OneToMany(mappedBy = "fraudAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FraudRuleResult> ruleResults = new ArrayList<>();
}
