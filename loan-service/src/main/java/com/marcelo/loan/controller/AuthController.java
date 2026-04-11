package com.marcelo.loan.controller;

import com.marcelo.loan.controller.mapper.CustomerMapper;
import com.marcelo.loan.controller.request.CustomerRequest;
import com.marcelo.loan.controller.response.CustomerResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Auth", description = "Endpoints para registro e consulta de clientes")
public class AuthController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PostMapping
    @Operation(summary = "Registrar novo cliente", description = "Cria um perfil de cliente vinculado ao Keycloak Id do token")
    public ResponseEntity<CustomerResponse> register(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CustomerRequest request) {
        String keycloakId = jwt.getSubject();
        Customer customer = customerMapper.toEntity(request, keycloakId);
        Customer saved = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDTO(saved));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter meus dados", description = "Retorna os dados do perfil do cliente autenticado")
    public ResponseEntity<CustomerResponse> getMyData(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        Customer customer = customerService.getByKeycloakId(keycloakId);
        return ResponseEntity.ok(customerMapper.toDTO(customer));
    }
}
