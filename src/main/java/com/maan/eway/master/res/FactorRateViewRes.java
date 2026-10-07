package com.maan.eway.master.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateViewRes {

	@JsonProperty("CoverList")
	private List<FactorRateCoverRes> coverList ;
	
	@JsonProperty("SubCoverList")
	private List<FactorRateSubCoverRes> subCoverList ;
}
