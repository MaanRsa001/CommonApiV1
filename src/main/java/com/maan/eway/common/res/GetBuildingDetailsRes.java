package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetBuildingDetailsRes {

	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;
	@JsonProperty("LocationId")
    private String    locationId   ;
	@JsonProperty("CustomerReferenceNo")
    private String     customerReferenceNo ;
	@JsonProperty("ProductId")
    private String    productId    ;
	@JsonProperty("SectionId")
    private String sectionId    ;
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	@JsonProperty("BranchCode")
    private String     branchCode   ;
	@JsonProperty("InbuildConstructType")
    private String     inbuildConstructType ;
	@JsonProperty("BuildingFloors")
    private String buildingFloors ;
	@JsonProperty("OutbuildConstructType")
    private String     outbuildConstructType ;
	@JsonProperty("BuildingUsageYn")
    private String     buildingUsageYn ;
	@JsonProperty("BuildingPurpose")
    private String     buildingPurpose;
	@JsonProperty("BuildingUsageDesc")
    private String     buildingUsageDesc ;
	@JsonProperty("BuildingPurposeId")
    private String     buildingPurposeId;
	@JsonProperty("BuildingUsageId")
    private String     buildingUsageId;

	
	
	@JsonProperty("BuildingType")
	private String     buildingType;

	@JsonProperty("BuildingOwnerYn")
	private String     buildingOwnerYn;
	@JsonProperty("PersonalAccidentSuminsured")
    private String     personalAccidentSuminsured ;
	@JsonProperty("PersonalIntermediarySuminsured")
    private String     personalIntermediarySuminsured ;
	
	
	@JsonProperty("BuildingOccupationType")
    private String     buildingOccupationType ;
	@JsonProperty("WithoutInhabitantDays")
    private String    withoutInhabitantDays ;

	@JsonProperty("BuildingCondition")
    private String     buildingCondition ;
	@JsonProperty("BuildingBuildYear")
    private String    buildingBuildYear ;
	
	@JsonProperty("BuidingAreaSqm")
    private String     buidingAreaSqm ;
	@JsonProperty("BuildingSuminsured")
    private String     buildingSuminsured ;
	@JsonProperty("AllriskSumInsured")
    private String     allriskSuminsured ;
	@JsonProperty("ContentSuminsured")
    private String     contentSuminsured ;
	
	@JsonProperty("Createdby")
    private String     createdBy    ;
	
	@JsonProperty("AcexecutiveId")
    private String    acExecutiveId ;
	@JsonProperty("ApplicationId")
    private String     applicationId ;
	@JsonProperty("BrokerCode")
    private String     brokerCode   ;
	@JsonProperty("SubUsertype")
    private String     subUserType  ;
	@JsonProperty("LoginId")
    private String     loginId      ;
	@JsonProperty("AgencyCode")
    private String     agencyCode   ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
    private Date       policyStartDate ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
    private Date       policyEndDate ;
	
	@JsonProperty("Currency")
    private String     currency     ;
	@JsonProperty("ExchangeRate")
    private String     exchangeRate ;
	@JsonProperty("BrokerBranchCode")
    private String     brokerBranchCode  ;
	
	@JsonProperty("Havepromocode")
    private String     havepromocode;
	
	@JsonProperty("Promocode")
    private String     promocode;
	
	@JsonProperty("InsuranceType")
    private String    insuranceType;
	
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("CategoryId")
    private String    categoryId;

	
	
	}
