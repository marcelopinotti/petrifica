package com.marcelo.loan.controller;

import com.marcelo.loan.controller.mapper.CustomerMapper;
import com.marcelo.loan.controller.response.CustomerResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Endpoints para consulta de perfil de cliente")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @GetMapping("/me")
    @Operation(summary = "Obter meus dados", description = "Retorna os dados do perfil do cliente autenticado")
    public ResponseEntity<CustomerResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
      //  Customer customer = customerService.getByKeycloakId(keycloakId);
        return null;
            //    ResponseEntity.ok(customerMapper.toDTO(customer));
    }
}
