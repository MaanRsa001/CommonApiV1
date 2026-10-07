package com.maan.eway.excelupload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfoGetReq {
	
	@JsonProperty("QuoteNo")
    private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;

}
