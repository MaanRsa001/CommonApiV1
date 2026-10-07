package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoInsuranceSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("CoInsuranceCompanyId")
	private String insuranceCompanyId;

	@JsonProperty("SharePercentage")
	private Double sharePercentage;

	@JsonProperty("LeaderYn")
	private String leaderYn;

}
