package com.marcelo.loan.controller.mapper;

import com.marcelo.loan.controller.request.CustomerRequest;
import com.marcelo.loan.controller.response.CustomerResponse;
import com.marcelo.loan.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request, String keycloakId) {
        return Customer.builder()
                .keycloakId(keycloakId)
                .fullName(request.fullName())
                .email(request.email())
                .cpf(request.cpf())
                .monthlyIncome(request.monthlyIncome())
                .build();
    }

    public CustomerResponse toDTO(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getCpf(),
                customer.getMonthlyIncome(),
                customer.getCreatedAt()
        );
    }
}
