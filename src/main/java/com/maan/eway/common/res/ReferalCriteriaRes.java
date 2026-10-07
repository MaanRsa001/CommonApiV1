package com.maan.eway.common.res;

import java.util.Date;

import lombok.Data;

@Data
public class ReferalCriteriaRes {

	// Customer Info
	private Long   idsCount ;
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
}
