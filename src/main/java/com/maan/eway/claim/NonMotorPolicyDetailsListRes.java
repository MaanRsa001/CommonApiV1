package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorPolicyDetailsListRes {
	
	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;

	@JsonProperty("EndtCount")
	private Integer endtCount;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;

	@JsonProperty("PolicyEndDate")
	private String policyEndDate;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("IdTypeName")
	private String idTypeName;
	
	@JsonProperty("IdNumber")
	private String idNumber;

}
