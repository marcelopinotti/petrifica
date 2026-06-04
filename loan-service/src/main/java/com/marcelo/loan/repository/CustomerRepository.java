package com.marcelo.loan.repository;

import com.marcelo.loan.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findByKeycloakId(String keycloakId);
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByEmail(String email);
}
