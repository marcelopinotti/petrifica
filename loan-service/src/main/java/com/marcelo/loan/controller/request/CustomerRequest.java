package com.marcelo.loan.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String cpf,
        @NotNull @Positive BigDecimal monthlyIncome
) {}
