package com.maan.eway.common.res;

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
public class TravelRejectCriteriaRes {
	// Customer Info
		private Integer   idsCount ;
	    private String   customerReferenceNo ;
	    private String idNumber;
		private String clientName;

		// Vehicle Info
		private String     companyId ;
		private String     productId ;
		private String     branchCode ;
		
		private String   requestReferenceNo ;
		private String quoteNo;
		private String customerId;
		private Date policyStartDate;
		private Date policyEndDate;
		private String rejectReason;
		private BigDecimal overallPremiumLc;
		private BigDecimal overallPremiumFc;
		private String currency;
}
