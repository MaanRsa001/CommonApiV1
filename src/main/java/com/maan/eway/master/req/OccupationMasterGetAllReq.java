package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationMasterGetAllReq implements Serializable {

    private static final long serialVersionUID = 1L;

/*    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset; */
   
    
    @JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("CategoryId")
	private String categoryId;

}
