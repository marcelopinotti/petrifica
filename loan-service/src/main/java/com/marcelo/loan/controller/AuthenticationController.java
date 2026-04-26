package com.marcelo.loan.controller;

import com.marcelo.loan.controller.mapper.CustomerMapper;
import com.marcelo.loan.controller.request.CustomerRequest;
import com.marcelo.loan.controller.response.CustomerResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticação e registro de usuários")
public class AuthenticationController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo cliente", description = "Cria um perfil de cliente vinculado ao Keycloak Id do token JWT")
    public ResponseEntity<CustomerResponse> register(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CustomerRequest request) {
        String keycloakId = jwt.getSubject();
        Customer customer = customerMapper.toEntity(request, keycloakId);
        Customer saved = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDTO(saved));
    }
}
