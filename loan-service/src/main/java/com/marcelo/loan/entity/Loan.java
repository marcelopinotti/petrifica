package com.marcelo.loan.entity;

import com.marcelo.loan.entity.enums.LoanReason;
import com.marcelo.loan.entity.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loans")
public class Loan {
    @Id
    private String id;

    private String customerId;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private Integer installments;
    private BigDecimal interestRate;
    private LoanReason reason;
    private LoanStatus status;

    @Builder.Default
    private List<StatusHistory> statusHistory = new ArrayList<>();
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
