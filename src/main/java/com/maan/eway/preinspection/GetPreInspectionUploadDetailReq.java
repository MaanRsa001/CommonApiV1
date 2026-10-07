package com.maan.eway.preinspection;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPreInspectionUploadDetailReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RegistrationNo")
	private String registrationNo;
	
	@JsonProperty("ReferenceNo")
	private String referenceNo;
	
	@JsonProperty("ImageName")
	private String imageName;
	
	@JsonProperty("ImageFilePath")
	private String imageFilePath;
	
	@JsonProperty("FromDate")
	private String fromDate;
	
	@JsonProperty("ToDate")
	private String toDate;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("SearchType")
	private String searchTYpe;
	
}
