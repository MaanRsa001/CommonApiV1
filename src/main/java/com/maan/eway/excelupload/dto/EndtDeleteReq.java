package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class EndtDeleteReq {
	
	@JsonProperty("EndtReqRefNo")
	private String endtReqRefNo;
	
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	
	@JsonProperty("ContentIds")
	private List<String> contentIds;
	
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
	
}
