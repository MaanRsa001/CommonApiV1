package com.maan.eway.claimintimation.dto;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
public class ClaimCoInsurancedetailsData {

	private String quoteNo;
	private Integer companyId;
	private Integer productId;
	private String productDesc;
	private String customerName;
	@Temporal(TemporalType.DATE)
	private Date policyStartDate;
	@Temporal(TemporalType.DATE)
	private Date policyEndDate;
	private BigDecimal sumInsuredLc;
	private BigDecimal sumInsuredFc;
	private BigDecimal totalPremiumLc;
	private BigDecimal totalPremiumFc;
	private BigDecimal taxAmountLc;
	private BigDecimal taxAmountFc;
	private String currencyId;
	private String status;
	private BigDecimal exchangeRate;

}
