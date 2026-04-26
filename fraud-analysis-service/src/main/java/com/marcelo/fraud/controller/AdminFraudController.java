package com.marcelo.fraud.controller;

import com.marcelo.fraud.controller.response.AnalysisStatsResponse;
import com.marcelo.fraud.service.FraudAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/frauds")
@RequiredArgsConstructor
@Secured("ROLE_ANALYST")
@Tag(name = "Admin - Fraud Analysis", description = "Endpoints administrativos para monitoramento de fraude (acesso restrito)")
public class AdminFraudController {

    private final FraudAnalysisService fraudAnalysisService;

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas Gerais de Fraude", description = "Retorna métricas globais: total de aprovações, rejeições e score de risco médio")
    public ResponseEntity<AnalysisStatsResponse> getStats() {
        return ResponseEntity.ok(fraudAnalysisService.getStats());
    }
}
