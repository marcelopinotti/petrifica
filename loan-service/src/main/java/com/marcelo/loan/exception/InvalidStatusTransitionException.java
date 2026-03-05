package com.marcelo.loan.exception;

import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(LoanStatus current, LoanEvent event) {
        super("Evento " + event + " não permitido a partir do status " + current);
    }
}
