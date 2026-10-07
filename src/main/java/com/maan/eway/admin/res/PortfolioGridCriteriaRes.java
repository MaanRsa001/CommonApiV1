package com.maan.eway.admin.res;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioGridCriteriaRes {


	// Customer Info

	private String customerReferenceNo;
	private String idNumber;
	private String clientName;
	private String middleName;
	private String lastName;
	private String titleDesc;
	private String mobileNo1;
	private String taxExemptedId;
	private String isTaxExempted;
	

	// Vehicle Info
	private String     companyId ;
	private int     productId ;
	private String     productName ;
	private String     branchCode ;
	
	private String   requestReferenceNo ;
	private String quoteNo;
	private String customerId;
	private Date inceptionDate;
	private Date expiryDate;
	private BigDecimal overallPremiumLc;
	private BigDecimal overallPremiumFc;
	private String policyNo;
	private String debitAcNo;
	private String debitTo;
	private String debitToId;

	private String debitNoteNo;

	private Date debitNoteDate;

	private String creditTo;

	private String creditToId;

	private String creditNo;

	private Date creditDate;
	
	private String emiYn;

	private String installmentPeriod;
	
	private String     noOfInstallment ;
	private String     paymentStatus ;
	private BigDecimal     emiPremium ;
	private Date effectiveDate;
	
	private String currency;
	private String originalPolicyNo;
	private String coreIntgStatus;
}

