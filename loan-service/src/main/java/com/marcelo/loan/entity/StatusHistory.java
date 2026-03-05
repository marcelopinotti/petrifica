package com.marcelo.loan.entity;

import com.marcelo.loan.entity.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {

    private LoanStatus status;
    private Instant changedAt;
    private String notes;
}
