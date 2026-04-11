package com.marcelo.loan.service;

import com.marcelo.loan.controller.mapper.LoanMapper;
import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.StatusHistory;
import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.event.FraudAnalysisResultEvent;
import com.marcelo.loan.event.LoanRequestedEvent;
import com.marcelo.loan.exception.LoanNotFoundException;
import com.marcelo.loan.exception.UnauthorizedOperationException;
import com.marcelo.loan.messaging.LoanEventProducer;
import com.marcelo.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanStateService loanStateService;
    private final CustomerService customerService;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final LoanEventProducer loanEventProducer;

    public LoanResponse createLoan(String keycloakId, LoanRequest request) {
        Customer customer = customerService.getByKeycloakId(keycloakId);
        Loan loan = loanMapper.toEntity(request, customer.getId());
        loanRepository.save(loan);
        changeStatus(loan, LoanEvent.SUBMIT, "Empréstimo solicitado");

        LoanRequestedEvent event = new LoanRequestedEvent(
                loan.getId(),
                customer.getId(),
                customer.getCpf(),
                customer.getMonthlyIncome(),
                loan.getRequestedAmount(),
                loan.getInstallments(),
                loan.getReason().name(),
                loan.getCreatedAt()
        );
        loanEventProducer.publishLoanRequested(event);
        
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

    public List<LoanResponse> getLoansByKeycloakId(String keycloakId) {
        Customer customer = customerService.getByKeycloakId(keycloakId);
        List<Loan> loans = loanRepository.findByCustomerId(customer.getId());
        return loans.stream()
                .map(loanMapper::toDTO)
                .toList();
    }

    public List<LoanResponse> getPendingLoans() {
        List<Loan> loans = loanRepository.findByStatus(LoanStatus.PENDING);
        return loans.stream()
                .map(loanMapper::toDTO)
                .toList();
    }

    public void applyFraudDecision(FraudAnalysisResultEvent event) {
        Loan loan = findLoanForFraudDecision(event);
        if (loan == null) {
            return;
        }

        if (isLoanAlreadyFinished(loan)) {
            log.info("Evento duplicado ou tardio ignorado: loanId={}, statusAtual={}",
                    event.loanId(), loan.getStatus());
            return;
        }

        LoanEvent decisionEvent = determineDecisionEvent(event.verdict());
        String notes = generateDecisionNotes(decisionEvent, event.rejectionReason());

        changeStatus(loan, decisionEvent, notes);
        
        log.info("Status atualizado por resultado de fraude: loanId={}, novoStatus={}",
                loan.getId(), loan.getStatus());
    }

    private Loan findLoanForFraudDecision(FraudAnalysisResultEvent event) {
        Loan loan = loanRepository.findById(event.loanId()).orElse(null);
        
        if (loan == null) {
            log.warn("Ignorando resultado de fraude para loan inexistente: loanId={}", event.loanId());
            return null;
        }

        if (!loan.getCustomerId().equals(event.customerId())) {
            log.error("Evento inconsistente: loanId={}, customerId esperado={}, customerId recebido={}",
                    event.loanId(), loan.getCustomerId(), event.customerId());
            return null;
        }
        
        return loan;
    }

    private boolean isLoanAlreadyFinished(Loan loan) {
        return loan.getStatus() == LoanStatus.APPROVED
                || loan.getStatus() == LoanStatus.REJECTED
                || loan.getStatus() == LoanStatus.CANCELLED;
    }

    private LoanEvent determineDecisionEvent(String verdict) {
        if (verdict == null) {
            throw new IllegalArgumentException("Veredito de fraude não pode ser nulo");
        }
        
        String cleanVerdict = verdict.trim().toUpperCase();
        
        if ("APPROVED".equals(cleanVerdict)) {
            return LoanEvent.APPROVE;
        }
        
        if ("REJECTED".equals(cleanVerdict)) {
            return LoanEvent.REJECT;
        }
        
        throw new IllegalArgumentException("Veredito de fraude inválido: " + verdict);
    }

    private String generateDecisionNotes(LoanEvent decisionEvent, String rejectionReason) {
        if (decisionEvent == LoanEvent.APPROVE) {
            return "Aprovado pela análise de fraude";
        }
        
        String notes = "Rejeitado pela análise de fraude: ";
        if (rejectionReason == null || rejectionReason.isEmpty()) {
            notes += "sem motivo informado";
        } else {
            notes += rejectionReason;
        }
        
        return notes;
    }

    private Loan findLoanById(String id){
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Empréstimo não encontrado: " + id));
    }
}
