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
public class PortfolioAdminSearchRes {

	// Customer Info
		private String customerReferenceNo;
		private String idNumber;
		private String clientName;
		private String mobileNo1;
		private String isTaxExempted;
		private String taxExemptedId;
		

		// Vehicle Info
		private String     companyId ;
		private Integer     productId ;
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
		

		private String noOfInstallment;
		
		private String paymentStatus;
		
		private Date effectiveDate;
		
		private String currency;
		private String originalPolicyNo;
		private String applicationId;
	
		
		
}
