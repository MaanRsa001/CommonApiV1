package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceSaveRes {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("TravelId")
	private String travelId;
	
	@JsonProperty("MSRefNo")
	private String msrefno;
	
	@JsonProperty("CdRefNo")
	private String cdRefNo;

	@JsonProperty("VdRefNo")
	private String vdRefNo;
		
	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	 
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SourceCountryDesc")
	private String sourceCountryDesc;
	
	@JsonProperty("DestinationCountryDesc")
	private String desctinationCountryDesc;
	
	@JsonProperty("OldRequestReferenceNo")
	private String oldRequestReferenceNo;

	@JsonProperty("GroupDetails")
	private List<TravelGroupGetRes> groupDetails;
}
