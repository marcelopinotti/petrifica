package com.marcelo.loan.controller;

import com.marcelo.loan.controller.mapper.CustomerMapper;
import com.marcelo.loan.controller.request.CustomerRequest;
import com.marcelo.loan.controller.response.CustomerResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CustomerRequest request) {
        String keycloakId = jwt.getSubject();
        Customer customer = customerMapper.toEntity(request, keycloakId);
        Customer saved = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDTO(saved));
    }
}
