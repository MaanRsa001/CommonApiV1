package com.maan.eway.workflow.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.maan.eway.workflow.util.LocalDateTimeTypeAdapter;

import lombok.Data;

@Data
public class WorkEngine {
	@SerializedName("CompanyId")
	@JsonProperty("CompanyId")
	private String companyId;
	@SerializedName("ProductId")
	@JsonProperty("ProductId")
	private String productId;
	
	@SerializedName("SectionId")
	@JsonProperty("SectionId") 
	private String sectionId;
	
	@SerializedName("CoverId")
	@JsonProperty("CoverId") 
	private Integer coverId;
	
	@SerializedName("RiskId")
	@JsonProperty("RiskId") 
	private Integer riskId;
	
	@SerializedName("VehicleId")
	@JsonProperty("VehicleId") 
	private String vehicleId;  
	@SerializedName("RequestReferenceNo")
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@SerializedName("LocationId")
	@JsonProperty("LocationId") 
	private String locationId; 

	@SerializedName("MSRefNo")
	@JsonProperty("MSRefNo") 
	private String msrefno;

	@SerializedName("CdRefNo")
	@JsonProperty("CdRefNo")
	private String cdRefNo;

	@SerializedName("VdRefNo")
	@JsonProperty("VdRefNo")
	private String vdRefNo;

	@SerializedName("CreatedBy")
	@JsonProperty("CreatedBy")
	private String createdBy;

	@SerializedName("QuoteNo")
	@JsonProperty("QuoteNo")
	private String quoteNo;

	@SerializedName("Integ_Type")
	@JsonProperty("Integ_Type")
	private String integType;

	@SerializedName("PolicyNo")
	@JsonProperty("PolicyNo")
	private String policyNo;

	@Override
	public String toString() {
		Gson gson = new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) .create();
		return gson.toJson(this);
	}

}
