package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CropListResp {

	@JsonProperty("CropId")
	private String cropId;
	
	@JsonProperty("CropDesc")
	private String cropDesc;
	
	@JsonProperty("PerHACost")
	private String perHACost;
}
