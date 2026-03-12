package com.marcelo.loan.entity;

import com.marcelo.loan.entity.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {

    private LoanStatus status;
    private Instant changedAt;
    private String notes;
}
