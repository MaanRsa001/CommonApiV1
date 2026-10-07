package com.maan.eway.upr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UprPolicyResponse {
	private Long         id;
    private String       policyNo;
    private String       lobCode;
    private String       lobname;
    private String       insuredName;
    private LocalDate    inceptionDate;
    private LocalDate    expiryDate;
    private Integer      policyTermDays;
    private BigDecimal   grossPremium;
    private BigDecimal   netPremium;
    private BigDecimal   commissionAmount;
    private String       status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
