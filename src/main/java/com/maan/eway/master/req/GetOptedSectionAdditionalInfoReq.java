package com.maan.eway.master.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetOptedSectionAdditionalInfoReq {

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("OptedSectionIds")
	private List<Integer> optedSectionIds;

}
