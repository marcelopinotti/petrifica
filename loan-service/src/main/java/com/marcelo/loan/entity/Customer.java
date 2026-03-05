package com.marcelo.loan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    @Indexed(unique = true)
    private String keycloakId;
    private String fullName;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String cpf;
    private BigDecimal monthlyIncome;
    @CreatedDate
    private Instant createdAt;
}
