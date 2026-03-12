package com.marcelo.loan.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String customerId) {
        super("Cliente com ID " + customerId + " não encontrado");
    }
}
