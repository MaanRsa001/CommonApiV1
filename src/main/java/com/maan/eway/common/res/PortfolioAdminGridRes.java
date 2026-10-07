package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAdminGridRes {

	
	private String creditNo;
	private String debitNoteNo;
	private String applicationId;
	private Long count;
	private BigDecimal overallPremiumLc;
	private BigDecimal overallPremiumFc;
	private String currencyCode;
	private BigDecimal exchangeRate;
	private String requestReferenceNo;
	private String quoteNo;
	private String policyNo;
	private String originalPolicyNo;
	private Integer productId;
	private String productName;
	private Integer oaCode;
	private String loginId;
	private String adminRemarks;
	private String referralRemarks;
	private String adminLoginId;
	private String status;
	private String endtStatus;
	private String customerName;
	private Date policyStartDate;
	private Date policyEndDate;
	private String branchCode;
	private String branchName;
	private String brokerBranchCode;
	private String brokerBranchName;
	private String brokerName;
	private String userType;
	private String subUserType;
	private Date updatedDate;
	private String endorsementRemarks;
	private Date effectiveDate;
	private BigDecimal sumInsured;
	private String paymentType;
	private String merchantReference;
	private String authTransRefNo;
	private String paymentId;
	private BigDecimal commissionPercentage;
	private BigDecimal commissionAmount;
	private String sourceType;
	private String userName;
	
	
}
