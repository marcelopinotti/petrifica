package com.marcelo.fraud.controller.mapper;

import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.entity.Analysis;
import com.marcelo.fraud.event.LoanRequestedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AnalysisMapper {

    public Analysis toEntity(LoanRequestedEvent event) {
        return Analysis.builder()
                .loanId(event.loanId())
                .customerId(event.customerId())
                .requestedAmount(event.requestedAmount())
                .declaredIncome(event.declaredIncome())
                .riskScore(0)
                .rulesApplied(new ArrayList<>())
                .build();
    }

    public AnalysisResponse toDTO(Analysis analysis) {
        return new AnalysisResponse(
                analysis.getId(),
                analysis.getLoanId(),
                analysis.getCustomerId(),
                analysis.getRiskScore(),
                analysis.getVerdict(),
                analysis.getRejectionReason(),
                analysis.getAnalyzedAt()
        );
    }
}
