package com.maan.eway.realpay.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
