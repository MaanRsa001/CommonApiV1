package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BranchMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("StateCode")
    private String    stateCode    ;
	@JsonProperty("CityName")
    private String    cityName ;
	@JsonProperty("RegulatoryCode")
    private String    regulatoryCode     ;
	@JsonProperty("Address1")
    private String    address1     ;
	@JsonProperty("Address2")
    private String    address2     ;
	@JsonProperty("Email")
    private String    email    ;
	@JsonProperty("MobileNumber")
    private String   mobileNumber;
	@JsonProperty("BranchType")
    private String    branchType  ;
	
	@JsonProperty("CountryId")
    private String    countryId ;
	
	@JsonProperty("SalePointCode")
    private String  salePointCode;

	@JsonProperty("PoBox")
    private String    poBox ;
	
	@JsonProperty("BelongingBranch")
	private String belongingBranch;
	
	@JsonProperty("OrginationCountryId")
	private Long originationCountryId;
	
	@JsonProperty("DestinationCountryId")
	private Long destinationCountryId;
	

}
