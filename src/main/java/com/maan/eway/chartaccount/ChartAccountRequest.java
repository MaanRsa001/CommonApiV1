package com.maan.eway.chartaccount;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChartAccountRequest {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ChartId")
	private String chartId;
	
	@JsonProperty("DiscountYn")
	private String discountYn;
	
	@JsonProperty("RequestRefNo")
	private String requestRefNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("UserOptedCoverReq")
	private List<UserOptedCoverReq> userOptedCoverReq;
	
	@JsonProperty("IsCheckMinimumPremium")
	private Boolean isCheckMinimumPremium;

}
