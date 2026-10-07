package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateGetReq {

	@JsonProperty("ProductId")
    private String productId    ;
	
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	
	@JsonProperty("SectionId")
    private String     sectionId;
	
	@JsonProperty("CoverId")
    private String     coverId    ;
	
	@JsonProperty("SubCoverId")
    private String     subCoverId    ;
	
	@JsonProperty("BranchCode")
    private String     branchCode    ;
	
	@JsonProperty("AgencyCode")
    private String     agencyCode    ;
	
	@JsonProperty("FactorTypeId")
    private String     factorTypeId    ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
