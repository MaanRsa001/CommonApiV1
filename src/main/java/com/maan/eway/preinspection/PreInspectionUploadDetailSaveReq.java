package com.maan.eway.preinspection;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PreInspectionUploadDetailSaveReq {

	@JsonProperty("ChassisNo")
	private String chassisNo;
	
	@JsonProperty("RegistrationNo")
	private String registrationNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SSSSSS")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SSSSSS")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SSSSSS")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
}
