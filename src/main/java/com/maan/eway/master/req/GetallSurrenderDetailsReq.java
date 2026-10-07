package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetallSurrenderDetailsReq {

	@JsonProperty("ProductId")
    private String     productId;
	
	@JsonProperty("SectionId")
    private String     sectionId;
	
	@JsonProperty("InsuranceId")
    private String     companyId;
	
	@JsonProperty("PolicyTerm")
    private Integer     policyTerm ;
}
