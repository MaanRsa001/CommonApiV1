package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class UpdateFactorRateReq1 {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	
	@JsonProperty("LocationDetails")
	private List<LocationDetailsReq> locations;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ProductId")
	private String productId;
	

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("GroupId")
	private Integer groupId;
	
	@JsonProperty("CommissionPercentage")
	private String commissionPercentage;
	
	@JsonProperty("VatCommissison")
	private String vatCommissison;
	
	@JsonProperty("AdminLoginId")
	private String adminLoginId;
	//BenifitCoverDetails
	@JsonProperty("BenefitCoverDetails")
	private List<BenifitCoverReq> benifitCoverReq;

}
