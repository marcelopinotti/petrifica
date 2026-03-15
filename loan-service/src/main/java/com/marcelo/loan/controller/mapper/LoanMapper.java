package com.marcelo.loan.controller.mapper;

import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.enums.LoanStatus;
import org.springframework.stereotype.Component;


@Component
public class LoanMapper {

    public Loan toEntity(LoanRequest request, String customerId) {
        return Loan.builder()
                .customerId(customerId)
                .requestedAmount(request.requestedAmount())
                .installments(request.installments())
                .reason(request.reason())
                .status(LoanStatus.PENDING)
                .build();
    }

    public LoanResponse toDTO(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getCustomerId(),
                loan.getRequestedAmount(),
                loan.getInstallments(),
                loan.getReason(),
                loan.getStatus(),
                loan.getCreatedAt()
        );
    }
}
