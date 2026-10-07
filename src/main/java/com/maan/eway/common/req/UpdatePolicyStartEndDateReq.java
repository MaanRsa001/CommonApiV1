package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdatePolicyStartEndDateReq {

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("PolicyEndDate")
//    private Date       policyEndDate ;
//	
	@JsonProperty("QuoteNo")
    private String   quoteNo;

	
}
