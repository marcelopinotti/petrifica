package com.marcelo.loan.service;

import com.marcelo.loan.controller.mapper.LoanMapper;
import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.StatusHistory;
import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.exception.LoanNotFoundException;
import com.marcelo.loan.exception.UnauthorizedOperationException;
import com.marcelo.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanStateService loanStateService;
    private final CustomerService customerService;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    public LoanResponse createLoan(String keycloakId, LoanRequest request) {
        Customer customer = customerService.getByKeycloakId(keycloakId);
        Loan loan = loanMapper.toEntity(request, customer.getId());
        loanRepository.save(loan);
        changeStatus(loan, LoanEvent.SUBMIT, "Empréstimo solicitado");
        return loanMapper.toDTO(loan);
    }

    public LoanResponse getLoanById(String keycloakId, String id) {
        Loan loan = findLoanById(id);
        Customer customer = customerService.getByKeycloakId(keycloakId);
        if (!loan.getCustomerId().equals(customer.getId())) {
            throw new UnauthorizedOperationException("Você não tem permissão para acessar este empréstimo");
        }
        return loanMapper.toDTO(loan);
    }

    public LoanResponse updateLoan(String keycloakId, String id, LoanRequest request) {
        Loan loan = findLoanById(id);
        Customer customer = customerService.getByKeycloakId(keycloakId);
        if (!loan.getCustomerId().equals(customer.getId())) {
            throw new UnauthorizedOperationException("Você não tem permissão para atualizar este empréstimo");
        }
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Somente empréstimos com status PENDING podem ser atualizados");
        }
        loan.setRequestedAmount(request.requestedAmount());
        loan.setInstallments(request.installments());
        loan.setReason(request.reason());
        loanRepository.save(loan);
        return loanMapper.toDTO(loan);
    }

    public void changeStatus(Loan loan, LoanEvent event, String notes) {
        LoanStatus newStatus = loanStateService.processEvent(loan.getStatus(), event);
        loan.setStatus(newStatus);
        loan.getStatusHistory().add(
                StatusHistory.builder()
                        .status(newStatus)
                        .changedAt(Instant.now())
                        .notes(notes)
                        .build()
        );
        loanRepository.save(loan);
    }

    public void cancelLoan(String keycloakId, String id) {
        Loan loan = findLoanById(id);
        Customer customer = customerService.getByKeycloakId(keycloakId);
        if (!loan.getCustomerId().equals(customer.getId())) {
            throw new UnauthorizedOperationException("Você não tem permissão para cancelar este empréstimo");
        }
        changeStatus(loan, LoanEvent.CANCEL, "Empréstimo cancelado");
    }

    public List<LoanResponse> getLoansByCustomer(String customerId) {
        List<Loan> loan = loanRepository.findByCustomerId(customerId);
        return loan.stream()
                .map(loanMapper::toDTO)
                .toList();
    }

    public List<LoanResponse> getPendingLoans() {
        List<Loan> loans = loanRepository.findByStatus(LoanStatus.PENDING);
        return loans.stream()
                .map(loanMapper::toDTO)
                .toList();
    }

    private Loan findLoanById(String id){
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Empréstimo não encontrado: " + id));
    }
}
