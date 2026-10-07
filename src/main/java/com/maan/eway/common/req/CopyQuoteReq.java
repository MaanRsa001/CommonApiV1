package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CopyQuoteReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("SearchKey")
	private String searchKey;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("ApplicationId")
	private String applicationId;

	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;

	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("BranchCode")
	private String branchCode;

//	@JsonProperty("BrokerBranchCode")
//	private String brokerBranchCode;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SearchValue")
	private String searchValue;

	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	
	@JsonProperty("EndtRemarks")
	private String endtRemarks;
	
	@JsonProperty("EndtEffectiveDate")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date endtEffectiveDate;
	
	
}
