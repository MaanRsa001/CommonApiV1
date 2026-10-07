package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SequenceGenerateReq {

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("TypeDesc")
	private String typeDesc;
	
	@JsonProperty("Params")
	private List<String> params;
}
