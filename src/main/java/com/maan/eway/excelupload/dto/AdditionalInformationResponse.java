package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalInformationResponse {
	
	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;
	
	@JsonProperty("CompanyId")
    private String companyId;
	
	@JsonProperty("ProductId")
    private String productId;
	
	@JsonProperty("ContentItems")
    private List<ContentItemResponse> contentItems;
}
