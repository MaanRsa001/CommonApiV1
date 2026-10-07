package com.maan.eway.endorsment.request;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Endorsment {
	
	@JsonProperty("PolicyNo")
	private String policyNo;	
	
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("ProductId")
	private BigDecimal productId;
	
	@JsonProperty("EndtType")
	private String endtType;
	
	@JsonProperty("EndtRemarks")
	private String endtRemarks;
	
	@JsonProperty("EndtEffectiveDate")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date endtEffectiveDate;
	
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ApplicationId")
	private String applicationId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("RegistrationNo")
	private String registrationNo;

}
