package com.marcelo.fraud.service;

import com.marcelo.fraud.controller.mapper.AnalysisMapper;
import com.marcelo.fraud.entity.Analysis;
import com.marcelo.fraud.entity.enums.Verdict;
import com.marcelo.fraud.event.LoanRequestedEvent;
import com.marcelo.fraud.messaging.FraudResultProducer;
import com.marcelo.fraud.repository.AnalysisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudAnalysisServiceRuleTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private AnalysisMapper analysisMapper;

    @Mock
    private FraudResultProducer fraudResultProducer;

    @InjectMocks
    private FraudAnalysisService fraudAnalysisService;

    @Test
    void shouldRejectWhenCustomerHasMoreThanOneApprovedLoanAndAmountIsHighVsIncome() {
        LoanRequestedEvent event = new LoanRequestedEvent(
                "loan-new",
                "customer-1",
                "12345678900",
                new BigDecimal("10000"),
                new BigDecimal("6000"),
                12,
                "HOME",
                Instant.now()
        );

        Analysis mapped = Analysis.builder()
                .loanId(event.loanId())
                .customerId(event.customerId())
                .requestedAmount(event.requestedAmount())
                .declaredIncome(event.customerMonthlyIncome())
                .build();

        Analysis previous1 = Analysis.builder().loanId("loan-a").customerId("customer-1").verdict(Verdict.APPROVED).build();
        Analysis previous2 = Analysis.builder().loanId("loan-b").customerId("customer-1").verdict(Verdict.APPROVED).build();

        when(analysisMapper.toEntity(event)).thenReturn(mapped);
        when(analysisRepository.findByCustomerId("customer-1")).thenReturn(List.of(previous1, previous2));
        when(analysisRepository.save(any(Analysis.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Analysis result = fraudAnalysisService.analyzeLoan(event);

        assertThat(result.getRiskScore()).isEqualTo(55);
        assertThat(result.getVerdict()).isEqualTo(Verdict.REJECTED);
        assertThat(result.getRejectionReason()).contains("mais de um emprestimo ativo");

        ArgumentCaptor<com.marcelo.fraud.event.FraudAnalysisResultEvent> captor =
                ArgumentCaptor.forClass(com.marcelo.fraud.event.FraudAnalysisResultEvent.class);
        verify(fraudResultProducer).publishFraudResult(captor.capture());
        assertThat(captor.getValue().verdict()).isEqualTo(Verdict.REJECTED);
    }

    @Test
    void shouldApproveWhenRiskScoreIsBelowThreshold() {
        LoanRequestedEvent event = new LoanRequestedEvent(
                "loan-ok",
                "customer-2",
                "99999999999",
                new BigDecimal("10000"),
                new BigDecimal("3000"),
                12,
                "OTHER",
                Instant.now()
        );

        Analysis mapped = Analysis.builder()
                .loanId(event.loanId())
                .customerId(event.customerId())
                .requestedAmount(event.requestedAmount())
                .declaredIncome(event.customerMonthlyIncome())
                .build();

        when(analysisMapper.toEntity(event)).thenReturn(mapped);
        when(analysisRepository.findByCustomerId("customer-2")).thenReturn(List.of());
        when(analysisRepository.save(any(Analysis.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Analysis result = fraudAnalysisService.analyzeLoan(event);

        assertThat(result.getRiskScore()).isEqualTo(0);
        assertThat(result.getVerdict()).isEqualTo(Verdict.APPROVED);
        verify(fraudResultProducer).publishFraudResult(any());
    }
}

