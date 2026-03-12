package com.marcelo.fraud.entity;

import com.marcelo.fraud.entity.enums.Verdict;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "rulesApplied")       //  vai evitar printar listas grandes
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // com o mesmo id são o mesmo documento
@Document( collection = "analysis")
public class Analysis {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String loanId;
    private String customerId;
    private BigDecimal requestedAmount;
    private BigDecimal declaredIncome;
    private Integer riskScore;
    private Verdict verdict;
    private String rejectionReason;
    private String notes;

    @Builder.Default   //  vai evitar null pointer exception
    private List<RuleApplied> rulesApplied = new ArrayList<>();

    @CreatedDate
    private Instant analyzedAt;
}
