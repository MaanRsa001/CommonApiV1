package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BuildingCopyRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("LocationId")
	private String locationId;
			
	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	 
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private List<String> sectionIds;
	
	@JsonProperty("OldRequestReferenceNo")
	private String oldRequestReferenceNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("EndtPrevQuoteNo")
	private String endtPrevQuoteNo;
	
	@JsonProperty("EndtPrevPolicyNo")
	private String endtPrevPolicyNo;
	
	@JsonProperty("EndtCount")
	private BigDecimal endtCount;
	
	@JsonProperty("EndtStatus")
	private String endtStatus;
	
	@JsonProperty("IsFinanceYn")
	private String isFinanceYn ;
	
	@JsonProperty("EndtCategDesc")
	private String endtCategoryDesc;
	
	@JsonProperty("EndtTypeDesc")
	private String endTypeDesc;
	
	@JsonProperty("EndtTypeId")
	private String endTypeId;
	
	@JsonProperty("EndtEffDate")
	private Date endtEffDate;
	
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	
	
	@JsonProperty("ApplicationId")
	private String applicationId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("PreviousRequestReferenceNo")
	private String previousRequestReferenceNo;
	
}
