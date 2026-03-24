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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
}
