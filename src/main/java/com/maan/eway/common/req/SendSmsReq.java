package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SendSmsReq {

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("MobileNoDesc")
	private String mobileNoDesc;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("NotifTemplateName")
	private String notifTemplateName;
	
	@JsonProperty("SmsSubject")
	private String smsSubject;
	
	@JsonProperty("SmsBoby")
	private String smsBody;
	
}
