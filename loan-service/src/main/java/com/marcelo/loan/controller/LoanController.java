package com.marcelo.loan.controller;

import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.service.LoanService;
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
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody LoanRequest request) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(keycloakId, request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LoanResponse> updateLoan(@AuthenticationPrincipal Jwt jwt, @PathVariable String id, @Valid @RequestBody LoanRequest request) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(loanService.updateLoan(keycloakId, id, request));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelLoan(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        String keycloakId = jwt.getSubject();
        loanService.cancelLoan(keycloakId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(loanService.getLoanById(keycloakId, id));
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanResponse>> getMyLoans(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(loanService.getLoansByKeycloakId(keycloakId));
    }

    @GetMapping("/pending")
    @Secured("ROLE_ANALYST")
    public ResponseEntity<List<LoanResponse>> getPendingLoans() {
        return ResponseEntity.ok(loanService.getPendingLoans());
    }
}
