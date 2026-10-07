package com.maan.eway.master.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgricultureCropListResp {

	@JsonProperty("DistrictId")
	private String districtId;
	
	@JsonProperty("DistrictDesc")
	private String districtDesc;
	
	@JsonProperty("AEZ")
	private String aez;
	
	@JsonProperty("CropList")
	private List<CropListResp> cropList; 
	
}
