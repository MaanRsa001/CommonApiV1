package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminReferalStatusReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("AdminLoginId")
	private String adminLoginId;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	
	@JsonProperty("RejectReason")
	private String rejectReason;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("CommissionModifyYn")
	private String commissionModifyYn;
	
	@JsonProperty("CommissionPercent")
	private String commissionPercent;
}
