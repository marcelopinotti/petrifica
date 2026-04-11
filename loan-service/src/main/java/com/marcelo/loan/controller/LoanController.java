package com.marcelo.loan.controller;

import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Endpoints para gerenciamento de empréstimos")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Solicitar um novo empréstimo", description = "Cria um empréstimo com status PENDING e inicia análise de fraude")
    public ResponseEntity<LoanResponse> createLoan(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody LoanRequest request) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(keycloakId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um empréstimo pendente", description = "Permite alterar valores de um empréstimo enquanto ele ainda está PENDING")
    public ResponseEntity<LoanResponse> updateLoan(@AuthenticationPrincipal Jwt jwt, @PathVariable String id, @Valid @RequestBody LoanRequest request) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(loanService.updateLoan(keycloakId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar um empréstimo", description = "Cancela um empréstimo antes que ele entre em análise")
    public ResponseEntity<Void> cancelLoan(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        String keycloakId = jwt.getSubject();
        loanService.cancelLoan(keycloakId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar detalhes de um empréstimo", description = "Retorna os dados e o histórico de status de um empréstimo específico")
    public ResponseEntity<LoanResponse> getLoanById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(loanService.getLoanById(keycloakId, id));
    }

    @GetMapping("/me")
    @Operation(summary = "Listar meus empréstimos", description = "Retorna todos os empréstimos solicitados pelo usuário autenticado")
    public ResponseEntity<List<LoanResponse>> getMyLoans(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(loanService.getLoansByKeycloakId(keycloakId));
    }

    @GetMapping("/pending")
    @Secured("ROLE_ANALYST")
    @Operation(summary = "Listar empréstimos pendentes (Analista)", description = "Retorna todos os empréstimos do sistema que estão com status PENDING")
    public ResponseEntity<List<LoanResponse>> getPendingLoans() {
        return ResponseEntity.ok(loanService.getPendingLoans());
    }
}
