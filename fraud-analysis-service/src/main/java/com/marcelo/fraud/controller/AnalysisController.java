package com.marcelo.fraud.controller;

import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.service.FraudAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
@Tag(name = "Fraud Analysis", description = "Endpoints para consulta de resultados de análise de fraude")
public class AnalysisController {

    private final FraudAnalysisService fraudAnalysisService;

    @GetMapping("/loans/{loanId}")
    @Operation(summary = "Obter análise de fraude por Empréstimo", description = "Busca o resultado detalhado da análise de fraude para um empréstimo específico")
    public ResponseEntity<AnalysisResponse> getAnalysisByLoanId(@PathVariable String loanId) {
        return ResponseEntity.ok(fraudAnalysisService.getAnalysisByLoanId(loanId));
    }

    @GetMapping("/customers/{customerId}")
    @Operation(summary = "Listar histórico de análises por Cliente", description = "Retorna o histórico completo de análises de fraude para um cliente específico")
    public ResponseEntity<List<AnalysisResponse>> getAnalysisByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(fraudAnalysisService.getAnalysisByCustomerId(customerId));
    }

}
