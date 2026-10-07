package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Transient;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EndorsementCriteriaRes {

		// Customer Info
	
	//private Long   idsCount ;   
		private String   customerReferenceNo ;
	    private String idNumber;
		private String clientName;
		// Vehicle Info
	//	private Long   idsCount ;
		private String     companyId ;
		private String     productId ;
		private String     branchCode ;
		
		private String   requestReferenceNo ;
		private String quoteNo;
		private String customerId;
		@JsonFormat(pattern = "dd/MM/yyyy")
		private Date policyStartDate;
		@JsonFormat(pattern = "dd/MM/yyyy")
		private Date policyEndDate;

		private Integer endorsementTypeId;
		private String endorsementDesc;
		private String endorsementCategoryDesc;
		@JsonFormat(pattern = "dd/MM/yyyy")
		private Date effectiveDate;
		private String endorsementStatus;
		private String policyNo;
		private String endorsementRemarks;
		private Date endorsementDate;
		
		private BigDecimal     overallPremiumLc ;
		private BigDecimal     overallPremiumFc ;
		private BigDecimal       endtPremium ;
		private String     currency ;
		
		private String     debitNoteNo;
		private String     creditNo;
        private Long uwSysId;
        private Long polIdx;

		@Transient
		private String emiYn;
}
