package com.marcelo.loan.service;

import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.exception.CustomerNotFoundException;
import com.marcelo.loan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        if (customerRepository.findByKeycloakId(customer.getKeycloakId()).isPresent()) {
            throw new CustomerNotFoundException("Cliente já registrado");
        }
        return customerRepository.save(customer);
    }

    public Customer getByKeycloakId(String keycloakId) {
        return customerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + keycloakId));
    }
}

