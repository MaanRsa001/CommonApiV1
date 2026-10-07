package com.maan.eway.preinspection;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PreInspectionDashboardRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RegistrationNo")
	private String registrationNo;
	
	@JsonProperty("ChassisNo")
	private String chassisNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ReferenceNo")
	private String referenceNo;
	
	@JsonProperty("ImageDetails")
	private List<GetPreInspectinUploadList> imageDetails;
}
