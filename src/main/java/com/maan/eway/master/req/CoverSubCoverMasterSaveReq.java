package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverSubCoverMasterSaveReq {


    @JsonProperty("SubCoverId")
    private String    subCoverId    ;

    @JsonProperty("CoverId")
    private String    coverId    ;
    
    @JsonProperty("ProductId")
    private String    productId    ;
  
	@JsonProperty("SectionId")
    private String    sectionId    ;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
}
