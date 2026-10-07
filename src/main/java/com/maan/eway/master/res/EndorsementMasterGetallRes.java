package com.maan.eway.master.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementMasterGetallRes {

	@JsonProperty("EndtTypeCategoryId")
	private String endtTypeCategoryId;
	
	@JsonProperty("EndtTypeCategory")
	private String endtTypeCategory;
			
	@JsonProperty("ProductId")
	private String productId;
		
	@JsonProperty("CompanyId")
	private String companyId;



	@JsonProperty("EndorsementMasterListRes")
	private List<EndorsementMasterListRes> EndorsementMasterListRes;
}
