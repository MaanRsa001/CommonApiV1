package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetSectionReq {
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("PrevPolicyNo")
	private String prevPolicyNo;
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("SectionId")
	private String sectionId;
	@JsonProperty("LocationId")
	private String locationId;
	
}
