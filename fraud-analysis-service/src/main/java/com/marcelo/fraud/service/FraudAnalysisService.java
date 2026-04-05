package com.marcelo.fraud.service;

import com.marcelo.fraud.controller.mapper.AnalysisMapper;
import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.controller.response.AnalysisStatsResponse;
import com.marcelo.fraud.entity.Analysis;
import com.marcelo.fraud.entity.RuleApplied;
import com.marcelo.fraud.entity.enums.Verdict;
import com.marcelo.fraud.event.LoanRequestedEvent;
import com.marcelo.fraud.exception.AnalysisNotFoundException;
import com.marcelo.fraud.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;

    public Analysis analyzeLoan(LoanRequestedEvent event) {
        log.info("Iniciando analise de fraude: loanId={}, customerId={}, amount={}",
                event.loanId(), event.customerId(), event.requestedAmount());

        Analysis analysis = analysisMapper.toEntity(event);
        
        // Lógica simplificada de análise de risco
        List<RuleApplied> rules = new ArrayList<>();
        int score = 0;

        // Regra 1: Valor > 50% da renda mensal -> +30 pontos
        BigDecimal income = event.customerMonthlyIncome();
        if (income != null && income.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal halfIncome = income.multiply(new BigDecimal("0.5"));
            if (event.requestedAmount().compareTo(halfIncome) > 0) {
                rules.add(createRule("VALOR_ALTO_RENDA", "Valor solicitado superior a 50% da renda mensal", 30));
                score += 30;
            }
        }

        // Regra 2: Valor > R$ 50.000 -> +40 pontos
        if (event.requestedAmount().compareTo(new BigDecimal("50000")) > 0) {
            rules.add(createRule("VALOR_ABSOLUTO_ALTO", "Valor solicitado superior a R$ 50.000", 40));
            score += 40;
        }

        // Regra 3: Parcelas > 48 -> +20 pontos
        if (event.installments() != null && event.installments() > 48) {
            rules.add(createRule("PARCELAS_LONGAS", "Número de parcelas superior a 48 meses", 20));
            score += 20;
        }

        analysis.setRiskScore(score);
        analysis.setRulesApplied(rules);
        
        if (score >= 50) {
            analysis.setVerdict(Verdict.REJECTED);
            String reasons = rules.stream().map(RuleApplied::getMessage).collect(Collectors.joining("; "));
            analysis.setRejectionReason(reasons);
        } else {
            analysis.setVerdict(Verdict.APPROVED);
        }

        Analysis saved = analysisRepository.save(analysis);

        log.info("Analise de fraude concluída: loanId={}, score={}, verdict={}",
                event.loanId(), score, saved.getVerdict());
        return saved;
    }

    private RuleApplied createRule(String name, String msg, int impact) {
        return RuleApplied.builder()
                .ruleName(name)
                .passed(false)
                .message(msg)
                .riskScoreImpact(impact)
                .appliedAt(Instant.now())
                .build();
    }

    public AnalysisResponse getAnalysisByLoanId(String loanId) {
        Analysis analysis = analysisRepository.findByLoanId(loanId)
                .orElseThrow(() -> new AnalysisNotFoundException("Análise não encontrada para o loan: " + loanId));
        return analysisMapper.toDTO(analysis);
    }

    public List<AnalysisResponse> getAnalysisByCustomerId(String customerId) {
        return analysisRepository.findByCustomerId(customerId)
                .stream()
                .map(analysisMapper::toDTO)
                .toList();
    }

    public AnalysisStatsResponse getStats() {
        List<Analysis> analyses = analysisRepository.findAll();
        long totalApproved = analyses.stream().filter(a -> a.getVerdict() == Verdict.APPROVED).count();
        long totalRejected = analyses.stream().filter(a -> a.getVerdict() == Verdict.REJECTED).count();
        double averageRiskScore = analyses.stream().mapToInt(Analysis::getRiskScore).average().orElse(0.0);
        return new AnalysisStatsResponse(totalApproved, totalRejected, averageRiskScore);
    }
}
