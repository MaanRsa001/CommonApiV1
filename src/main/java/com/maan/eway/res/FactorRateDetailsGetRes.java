package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.req.CoverListGetRes;

import lombok.Data;

@Data
public class FactorRateDetailsGetRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CdRefNo")
	private String cdRefNo;

	@JsonProperty("VdRefNo")
	private String vdRefNo;
	
	@JsonProperty("MsRefNo")
	private String msRefNo;
	
	
	@JsonProperty("CoverList")
	private List<CoverListGetRes>  coverList ;
}

