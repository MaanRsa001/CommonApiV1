package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteRes {

	
	
	@JsonProperty("RiskDetails")
	private Object  riskDetails ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RenewalOldExpDate")
	private Date renewalOldExpDate;

	@JsonProperty("RenewalDateYn")
	private String renewalDateYn;

	@JsonProperty("RenewalStatus")
	private String renewalStatus;

	@JsonProperty("RenewalOldPolicy")
	private String renewalOldPolicy;
	 
}
