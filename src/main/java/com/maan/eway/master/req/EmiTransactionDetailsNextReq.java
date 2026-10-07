package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiTransactionDetailsNextReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	

	
}
