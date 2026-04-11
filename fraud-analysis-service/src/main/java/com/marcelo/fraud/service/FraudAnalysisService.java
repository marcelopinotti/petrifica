package com.marcelo.fraud.service;

import com.marcelo.fraud.controller.mapper.AnalysisMapper;
import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.controller.response.AnalysisStatsResponse;
import com.marcelo.fraud.entity.Analysis;
import com.marcelo.fraud.entity.RuleApplied;
import com.marcelo.fraud.entity.enums.Verdict;
import com.marcelo.fraud.event.FraudAnalysisResultEvent;
import com.marcelo.fraud.event.LoanRequestedEvent;
import com.marcelo.fraud.exception.AnalysisNotFoundException;
import com.marcelo.fraud.messaging.FraudResultProducer;
import com.marcelo.fraud.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final FraudResultProducer fraudResultProducer;

    public Analysis analyzeLoan(LoanRequestedEvent event) {
        log.info("Iniciando analise de fraude: loanId={}, customerId={}, amount={}",
                event.loanId(), event.customerId(), event.requestedAmount());

        Analysis analysis = analysisMapper.toEntity(event);
        List<RuleApplied> rules = new ArrayList<>();

        // Executar regras
        checkIncomeRule(event, rules);
        checkAmountRule(event, rules);
        checkExistingLoansRule(event, rules);
        checkInstallmentsRule(event, rules);

        // Calcular Score Total
        int totalScore = calculateTotalScore(rules);
        analysis.setRiskScore(totalScore);
        analysis.setRulesApplied(rules);

        // Definir Veredito
        if (totalScore >= 50) {
            analysis.setVerdict(Verdict.REJECTED);
            analysis.setRejectionReason(formatRejectionReasons(rules));
        } else {
            analysis.setVerdict(Verdict.APPROVED);
        }

        Analysis saved = analysisRepository.save(analysis);
        log.info("Analise de fraude concluída: loanId={}, score={}, verdict={}",
                event.loanId(), totalScore, saved.getVerdict());

        publishResult(saved);

        return saved;
    }

    private void checkIncomeRule(LoanRequestedEvent event, List<RuleApplied> rules) {
        BigDecimal income = event.customerMonthlyIncome();
        if (income != null && income.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal limit = income.multiply(new BigDecimal("0.5"));
            if (event.requestedAmount().compareTo(limit) > 0) {
                rules.add(createRule("VALOR_ALTO_RENDA", "Valor solicitado superior a 50% da renda mensal", 30));
            }
        }
    }

    private void checkAmountRule(LoanRequestedEvent event, List<RuleApplied> rules) {
        if (event.requestedAmount().compareTo(new BigDecimal("50000")) > 0) {
            rules.add(createRule("VALOR_ABSOLUTO_ALTO", "Valor solicitado superior a R$ 50.000", 40));
        }
    }

    private void checkExistingLoansRule(LoanRequestedEvent event, List<RuleApplied> rules) {
        long approvedCount = 0;
        List<Analysis> history = analysisRepository.findByCustomerId(event.customerId());
        for (Analysis a : history) {
            if (a.getVerdict() == Verdict.APPROVED) {
                approvedCount++;
            }
        }

        if (approvedCount > 1) {
            rules.add(createRule("MULTIPLOS_EMPRESTIMOS_ATIVOS", "Cliente possui mais de um emprestimo ativo", 25));
        }
    }

    private void checkInstallmentsRule(LoanRequestedEvent event, List<RuleApplied> rules) {
        if (event.installments() != null && event.installments() > 48) {
            rules.add(createRule("PARCELAS_LONGAS", "Número de parcelas superior a 48 meses", 20));
        }
    }

    private int calculateTotalScore(List<RuleApplied> rules) {
        int score = 0;
        for (RuleApplied rule : rules) {
            score += rule.getRiskScoreImpact();
        }
        return score;
    }

    private String formatRejectionReasons(List<RuleApplied> rules) {
        StringBuilder reasons = new StringBuilder();
        for (int i = 0; i < rules.size(); i++) {
            reasons.append(rules.get(i).getMessage());
            if (i < rules.size() - 1) {
                reasons.append("; ");
            }
        }
        return reasons.toString();
    }

    private void publishResult(Analysis saved) {
        fraudResultProducer.publishFraudResult(new FraudAnalysisResultEvent(
                saved.getLoanId(),
                saved.getCustomerId(),
                saved.getVerdict(),
                saved.getRejectionReason()
        ));
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
        long totalApproved = 0;
        long totalRejected = 0;
        int totalScore = 0;

        for (Analysis a : analyses) {
            if (a.getVerdict() == Verdict.APPROVED) {
                totalApproved++;
            } else if (a.getVerdict() == Verdict.REJECTED) {
                totalRejected++;
            }
            totalScore += a.getRiskScore();
        }

        double averageRiskScore = 0.0;
        if (!analyses.isEmpty()) {
            averageRiskScore = (double) totalScore / analyses.size();
        }

        return new AnalysisStatsResponse(totalApproved, totalRejected, averageRiskScore);
    }
}
