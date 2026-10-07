package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.SectionDetails;

import lombok.Data;

@Data
public class EserviceCommonGetRes {

	 @JsonProperty("RiskId")
	private  String riskId;	

	@JsonProperty("SalaryPerAnnum")
    private String     salaryPerAnnum ;
	
	@JsonProperty("BenefitCoverMonth")
    private String     benefitCoverMonth;
	
	@JsonProperty("SumInsured")
    private String     sumInsured ;
		
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("CategoryId")
    private String    categoryId;
	
	@JsonProperty("CustomerName")
    private String    customerName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("Dob")
    private Date    dob;

	@JsonProperty("JobJoiningMonth")
    private String    jobJoiningMonth;
	
	@JsonProperty("BetweenDiscontinued")
    private String    betweenDiscontinued;
	
	@JsonProperty("EthicalWorkInvolved")
    private String    ethicalWorkInvolved;

	@JsonProperty("EndorsementYn")
    private String     endorsementYn;
	
	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("SectionDetails")
	private List<SectionDetails>    sectionDetails;

	@JsonProperty("PremiumLc")
    private Double premiumLc;	

	@JsonProperty("PremiumFc")
    private Double premiumFc;	

	@JsonProperty("OverAllPremiumFc")
    private Double overAllPremiumFc;	

	@JsonProperty("OverAllPremiumLc")
    private Double overAllPremiumLc;	

	@JsonProperty("CommissionAmount")
    private String commissionAmount;	

	@JsonProperty("CommissionPercentage")
    private String commissionPercentage;
	
	 
    @JsonProperty("IndustryName")
	private String       industryName;
	
	@JsonProperty("NatureOfBusinessId")
	private String       natureOfBusinessId ;
	    
	@JsonProperty("NatureOfBusinessDesc")
	private String       natureOfBusinessDesc;
	    
	@JsonProperty("TotalNoOfEmployees")
	private String       totalNoOfEmployees;
	    
	@JsonProperty("TotalExcludedEmployees")
	private String       totalExcludedEmployees ;
	    
	@JsonProperty("TotalRejoinedEmployees")
	private String       totalRejoinedEmployees ;
	    
	@JsonProperty("AccountOutstandingEmployees")
	private String       accountOutstandingEmployees;
	    
	@JsonProperty("AccountAuditentType")
	private String       accountAuditentType ;
	    
	@JsonProperty("AuditentTypeDesc")
	private String       auditentTypeDesc ;

	@JsonProperty("TotalOutstandingAmount")
	private String       totalOutstandingAmount;
	

	@JsonProperty("EmpLiabilitySi")
    private String empLiabilitySi    ;
	
	@JsonProperty("LiabilityOccupationId")
    private String liabilityOccupationId    ;
	
	@JsonProperty("FidEmpSi")
    private String fidEmpSi    ;
	
	@JsonProperty("FidEmpCount")
    private String fidEmpCount    ;
	
	@JsonProperty("VatCommission")
    private String vatCommission;
	
	@JsonProperty("PolicyNo")
    private String policyNo;
	
	@JsonProperty("FinalyzeYn")
	private String finalizeYn;
	
	@JsonProperty("LocationId")
    private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	

	@JsonProperty("CategoryDesc")
	private String categoryDesc;
}
