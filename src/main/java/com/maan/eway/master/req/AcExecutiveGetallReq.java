package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveGetallReq {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
		
	@JsonProperty("CompanyId")
	private String companyId;
	
		
}
