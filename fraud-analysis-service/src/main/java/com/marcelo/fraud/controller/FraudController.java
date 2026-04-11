package com.marcelo.fraud.controller;

import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.controller.response.AnalysisStatsResponse;
import com.marcelo.fraud.service.FraudAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/frauds")
@RequiredArgsConstructor
@Tag(name = "Fraud Analysis", description = "Endpoints para consulta de resultados de análise de fraude")
public class FraudController {

    private final FraudAnalysisService fraudAnalysisService;

    @GetMapping("/loans/{loanId}")
    @Operation(summary = "Obter análise por Loan ID", description = "Busca o resultado detalhado da análise de fraude para um empréstimo específico")
    public ResponseEntity<AnalysisResponse> getAnalysisByLoanId(@PathVariable String loanId) {
        return ResponseEntity.ok(fraudAnalysisService.getAnalysisByLoanId(loanId));
    }

    @GetMapping("/customers/{customerId}")
    @Operation(summary = "Listar análises por Cliente", description = "Retorna o histórico de análises de fraude para um cliente específico")
    public ResponseEntity<List<AnalysisResponse>> getAnalysisByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(fraudAnalysisService.getAnalysisByCustomerId(customerId));
    }

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas Gerais", description = "Retorna métricas globais de aprovações, rejeições e score médio")
    public ResponseEntity<AnalysisStatsResponse> getStats() {
        return ResponseEntity.ok(fraudAnalysisService.getStats());
    }
}
