package com.maan.eway.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerTrackDashboadReq {

	@JsonProperty("SearchDate")
	private String searchDate;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("StatusCode")
	private String statusCode;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	
	@JsonProperty("AdminStatus")
	private String adminStatus;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
}
