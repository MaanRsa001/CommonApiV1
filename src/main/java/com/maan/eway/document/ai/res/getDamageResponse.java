package com.maan.eway.document.ai.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class getDamageResponse {
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("CompanyId")
    private String companyId;
	@JsonProperty("ProductId")
    private Integer productId;
	@JsonProperty("SectionList")
    private List<SectionDTO> sectionList;
}
