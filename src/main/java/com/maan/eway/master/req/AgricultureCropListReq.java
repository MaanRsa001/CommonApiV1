package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgricultureCropListReq {

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("Region")
	private String region;
	
	@JsonProperty("ProductId")
	private String productId;
}
