package com.marcelo.fraud.controller;

import com.marcelo.fraud.controller.response.AnalysisResponse;
import com.marcelo.fraud.controller.response.AnalysisStatsResponse;
import com.marcelo.fraud.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/frauds")
@RequiredArgsConstructor
public class FraudController {

    private final FraudAnalysisService fraudAnalysisService;

    @GetMapping("/loans/{loanId}")
    public ResponseEntity<AnalysisResponse> getAnalysisByLoanId(@PathVariable String loanId) {
        return ResponseEntity.ok(fraudAnalysisService.getAnalysisByLoanId(loanId));
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<AnalysisResponse>> getAnalysisByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(fraudAnalysisService.getAnalysisByCustomerId(customerId));
    }

    @GetMapping("/stats")
    public ResponseEntity<AnalysisStatsResponse> getStats() {
        return ResponseEntity.ok(fraudAnalysisService.getStats());
    }
}
