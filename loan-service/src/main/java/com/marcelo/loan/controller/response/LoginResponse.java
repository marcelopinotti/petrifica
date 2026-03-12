package com.marcelo.loan.controller.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {}
