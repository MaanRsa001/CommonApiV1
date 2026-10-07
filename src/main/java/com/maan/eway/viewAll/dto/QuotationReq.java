package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuotationReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
}
