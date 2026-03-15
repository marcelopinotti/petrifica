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
import com.marcelo.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    public LoanResponse getLoanById(String id) {
        return loanMapper.toDTO(findLoanById(id));
    }

    public LoanResponse updateLoan(String id, LoanRequest request) {
        Loan loan = findLoanById(id);
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

    public void cancelLoan(String id) {
        Loan loan = findLoanById(id);
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
