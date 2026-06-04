package com.marcelo.loan.entity;

import com.marcelo.loan.entity.enums.LoanReason;
import com.marcelo.loan.entity.enums.LoanStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loans")
public class Loan {

    public Loan() {

    }
    public Loan(UUID id, Customer customer, Device device, BigDecimal requestedAmount, BigDecimal approvedAmount, Integer installments, BigDecimal interestRate, BigDecimal declaredIncome, LoanReason reason, LoanStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customer = customer;
        this.device = device;
        this.requestedAmount = requestedAmount;
        this.approvedAmount = approvedAmount;
        this.installments = installments;
        this.interestRate = interestRate;
        this.declaredIncome = declaredIncome;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    private Integer installments;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "declared_income")
    private BigDecimal declaredIncome;

    @Enumerated(EnumType.STRING)
    private LoanReason reason;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


}
