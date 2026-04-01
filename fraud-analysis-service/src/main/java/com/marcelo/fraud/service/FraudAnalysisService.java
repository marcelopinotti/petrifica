package com.marcelo.fraud.service;

import com.marcelo.fraud.controller.mapper.AnalysisMapper;
import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.controller.response.AnalysisStatsResponse;
import com.marcelo.fraud.entity.Analysis;
import com.marcelo.fraud.entity.enums.Verdict;
import com.marcelo.fraud.exception.AnalysisNotFoundException;
import com.marcelo.fraud.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;

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
