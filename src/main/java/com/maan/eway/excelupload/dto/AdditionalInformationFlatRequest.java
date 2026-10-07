package com.maan.eway.excelupload.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.endorsment.request.NonMotEndtReq;

import lombok.Data;

@Data
public class AdditionalInformationFlatRequest {

	@JsonProperty("QuoteNo")
    private String quoteNo;
	
	@JsonProperty("CompanyId")
    private String companyId;
	@JsonProperty("ProductId")
    private String productId;
	@JsonProperty("LocationId")
    private String locationId;
	@JsonProperty("SectionId")
    private String sectionId;
	@JsonProperty("CoverId")
    private String coverId;
	@JsonProperty("RiskId")
    private String riskId;
	@JsonProperty("AdditionalInfo")
    private List<Map<String, Object>> additionaInfo;
	
	@JsonProperty("EndorsementDetails")
	private NonMotEndtReq nonMotEndtReq;
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	@JsonProperty("EndtPolicyNo")
	private String endtPolicyNo;
	@JsonProperty("EndtReqRefNo")
	private String endtReqRefNo;
	
	
}
