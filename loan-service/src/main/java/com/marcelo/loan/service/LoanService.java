package com.marcelo.loan.service;

import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.StatusHistory;
import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanStateService loanStateService;
    private final LoanRepository loanRepository;

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
}
