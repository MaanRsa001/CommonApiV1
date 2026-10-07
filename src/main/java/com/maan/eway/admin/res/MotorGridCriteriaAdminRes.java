package com.maan.eway.admin.res;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class MotorGridCriteriaAdminRes {


	// Customer Info
//	private Long   idsCount ;
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
	private String adminRemarks;
	private String status;
	private Date entryDate;
	
	private String endorsementType;
	private String endorsementTypeDesc;
	private Date endorsementDate;
	private String endorsementRemarks;
	private Date endorsementEffdate;
	private String originalPolicyNo;
	private String endtPrevPolicyNo;
	private String endtPrevQuoteNo;
	private BigDecimal endtCount;
	private String endtStatus;
	private String endtCategDesc;
	private String endorsementYn;
	private Double endtPremium;
	
}
