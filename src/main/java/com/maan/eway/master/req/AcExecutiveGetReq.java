package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveGetReq {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
		
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
		
}
