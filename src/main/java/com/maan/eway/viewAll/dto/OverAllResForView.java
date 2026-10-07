package com.maan.eway.viewAll.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.jasper.res.getEmiDetailsListRes;

import lombok.Data;

@Data
public class OverAllResForView {
	
	@JsonProperty("InceptionDate")
	private String inceptionDate ;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("PolicyNo")
	private String policyNumn;
	
	@JsonProperty("CurrencyName")
	private String currencyName;
	
	@JsonProperty("CountryName")
	private String countryName;
	
	@JsonProperty("ApprovedBy")
	private String approvedBy;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	private String policyNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("EffectiveDate")
	private String effectiveDate;
	
	@JsonProperty("PersonalInformation")
	private List<KeyAndValueDto> pi; 
	
	@JsonProperty("BrokerInformation")
	private List<KeyAndValueDto> brokerInfo; 
	
	@JsonProperty("PolicyInformation")
	private List<KeyAndValueDto> policyInfo; 
	
	@JsonProperty("CompanyInformation")
	private CompanyInfoDto company; 
	
	@JsonProperty("Locations")
	private List<LocationInformationKeyValueRes> location;
	
	@JsonProperty("AttachmentList")
	private List<AttachMentRes> attachment;
	
	@JsonProperty("CommonConditions")
	private CommonCWESetRes comCon; 
	
	@JsonProperty("TotalPremiumList")
	private List<Map<String, Object>> totalPre; 
	
	@JsonProperty("EmiDetails")
	private List<getEmiDetailsListRes> emi;
	
	@JsonProperty("emiYn")
	private String emiYn;
	
	@JsonProperty("VehicleIds")
	private List<List<KeyAndValueDto>> vehicleIds;
	
	

}
