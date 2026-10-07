package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.endorsment.request.NonMotEndtReq;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class additionalInfoGetResponse {

	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("LocationList")
	private List<LocationResponse> locationList;
	@JsonProperty("EndorsementDetails")
	private NonMotEndtReq nonMotEndtReq;
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	@JsonProperty("EndtPolicyNo")
	private String endtPolicyNo;
	@JsonProperty("EndtReqRefNo")
	private String endtReqRefNo;
	
}
