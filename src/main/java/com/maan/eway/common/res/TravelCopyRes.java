package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TravelCopyRes {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
		
	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	 
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SourceCountryDesc")
	private String sourceCountryDesc;
	
	@JsonProperty("DestinationCountryDesc")
	private String desctinationCountryDesc;
	
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
	
	@JsonProperty("GroupDetails")
	private List<TravelGroupGetRes> groupDetails;
}
