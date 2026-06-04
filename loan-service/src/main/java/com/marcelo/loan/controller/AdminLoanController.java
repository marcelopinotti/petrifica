package com.marcelo.loan.controller;

import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/loans")
@Secured("ROLE_ANALYST")
@Tag(name = "Admin - Loans", description = "Endpoints administrativos para análise de empréstimos (acesso restrito)")
public class AdminLoanController {

    private final LoanService loanService;

    public AdminLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/pending")
    @Operation(summary = "Listar empréstimos para análise", description = "Retorna empréstimos em status PENDING e UNDER_ANALYSIS para análise de fraude")
    public ResponseEntity<List<LoanResponse>> getPendingLoans() {
        return ResponseEntity.ok().build();
    }

    //loanService.getPendingLoans()
}
