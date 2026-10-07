package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ViewLoginDetailsRes {

	@JsonProperty("ProductDetails")
	private List<CountRes> productDetails;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("LastLoginDate")
	private Date lastLoginDate;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("LastPolicyDate")
	private Date lastPolicyDate;
	
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("LastQuoteDate")
	private Date lastQuoteDate;
	
	@JsonProperty("CollectedPremium")
	private String collectedPremium;
	
	@JsonProperty("PolicyCommission")
	private String policyCommission;
	
	
	
	
}
