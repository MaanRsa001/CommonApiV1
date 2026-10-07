package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UploadReq {
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("FileName")
	private String fileName;

}
