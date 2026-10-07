package com.maan.eway.realpay.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountDTO {
    private Long id;
    private LocalDate actionDate;
    private String bankCode;
    private String branchCode;
    private String accountType;
    private String accountNumber;
    private String idNumber;
    private String initials;
    private String clientName;
    private String email;
}
