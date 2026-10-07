package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetProductMasterReq {

	@JsonProperty("Limit")
	 private Integer limit;
	  
	  @JsonProperty("Offset")
	  private Integer offset;
	  
	  @JsonProperty("ProductId")
	  private String productid;
	  
	 @JsonProperty("CompanyId")
	 private String companyId;
	 
	 @JsonProperty("IndustryType")
	 private String IndsutryTypeId;
	 
	 
	 @JsonProperty("SectionId")
	 private String sectionId;
	 
	 @JsonProperty("LoginId")
	 private String loginId;
}
