package com.marcelo.loan.entity;

import com.marcelo.loan.entity.enums.LoanStatus;


import java.time.Instant;


public class StatusHistory {

    private LoanStatus status;
    private Instant changedAt;
    private String notes;
}
