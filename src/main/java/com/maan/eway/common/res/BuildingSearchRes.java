package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BuildingSearchRes {
	
	    @JsonProperty("RequestReferenceNo")
	    private String     requestReferenceNo ;

	    @JsonProperty("RiskId")
	    private Integer    riskId ;

	    @JsonProperty("SectionId")
	    private String    sectionId ;

	    @JsonProperty("QuoteNo")
	    private String     quoteNo ;

	    @JsonProperty("PolicyNo")
	    private String     policyNo;

	    @JsonProperty("LocationName")
	    private String     locationName;
	    
	    @JsonProperty("BuildingAddress")
	    private String     buildingAddress;

	    @JsonProperty("SectionDesc")
	    private String  sectionDesc;
	    
	    @JsonProperty("InbuildConstructType")
	    private String     inbuildConstructType ;

	    @JsonProperty("InbuildConstructTypeDesc")
	    private String     inbuildConstructTypeDesc ;

	    @JsonProperty("BuildingFloors")
	    private Integer    buildingFloors ;
	    
	    @JsonProperty("BuildingUsageId")
	    private String     buildingUsageId;
	    
	    @JsonProperty("BuildingUsageDesc")
	    private String     buildingUsageDesc;
	    
	    @JsonProperty("BuildingUsageYn")
	    private String     buildingUsageYn ;

	    private String     buildingType;
	    
	    @JsonProperty("BuildingOccupationType")
	    private String     buildingOccupationType ; 
	    
	    @JsonProperty("ApartmentOrBorder")
	    private String     apartmentOrBorder ;

	    @JsonProperty("WithoutInhabitantDays")
	    private Integer    withoutInhabitantDays ;
	    
	    @JsonProperty("BuildingCondition")
	    private String     buildingCondition ;

	    @JsonProperty("BuildingBuildYear")
	    private Integer    buildingBuildYear ;
	    
	    @JsonProperty("BuildingAge")
	    private Integer    buildingAge ;

	    @JsonProperty("BuildingAreaSqm")
	    private Double     buildingAreaSqm ;

	    @JsonProperty("BuildingSuminsured")
	    private BigDecimal     buildingSuminsured ;

	    @JsonProperty("EntryDate")
	    private Date       entryDate ;

	    @JsonProperty("CreatedBy")
	    private String     createdBy ;

	    @JsonProperty("Status")
	    private String     status ;

	    @JsonProperty("UpdatedDate")
	    private Date       updatedDate ;

	    @JsonProperty("UpdatedBy")
	    private String     updatedBy ;


	    @JsonProperty("CustomerId")
	    private String     customerId ;


	    @JsonProperty("BankCode")
	    private String   bankCode;
	    
	    @JsonProperty("EndorsementType")
	   private Integer    endorsementType ;

	    @JsonProperty("EndorsementTypeDesc")
	   private String     endorsementTypeDesc ;
	    
	    @JsonProperty("EndorsementDate")
	   private Date       endorsementDate ;

	    @JsonProperty("EndorsementRemarks")
	   private String     endorsementRemarks ;

	    @JsonProperty("EndorsementEffdate")
	   private Date       endorsementEffdate ;

	    @JsonProperty("OriginalPolicyNo")
	   private String     originalPolicyNo ;

	    @JsonProperty("EndtPrevPolicyNo")
	   private String     endtPrevPolicyNo ;

	    @JsonProperty("EndtPrevQuoteNo")
	   private String     endtPrevQuoteNo ;

	    @JsonProperty("EndtCount")
	   private BigDecimal endtCount ;

	    @JsonProperty("EndtStatus")
	   private String     endtStatus ;
	   
	    @JsonProperty("IsFinaceYn")
	   private String     isFinaceYn ;
	   
	   
	    @JsonProperty("EndtCategDesc")
	   private String     endtCategDesc ;

}
