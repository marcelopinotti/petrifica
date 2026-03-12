package com.marcelo.loan.repository;

import com.marcelo.loan.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByKeycloakId(String keycloakId);
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByEmail(String email);
}
