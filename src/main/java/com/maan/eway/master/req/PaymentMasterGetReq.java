package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@JsonProperty("PaymentMasterId")
	private String paymentMasterId;
    
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
}
