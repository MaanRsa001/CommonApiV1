package com.maan.eway.upr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UprPolicyInsertReq {

	private String policyNo;

	private String lobCode;

	private String lobname;

	private String insuredName;

	private LocalDate inceptionDate;

	private LocalDate expiryDate;

	private BigDecimal grossPremium;

	private BigDecimal netPremium;

	private String policyterm;

	private BigDecimal commissionAmount = BigDecimal.ZERO;
}
