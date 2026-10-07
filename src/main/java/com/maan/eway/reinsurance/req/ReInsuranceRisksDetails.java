package com.maan.eway.reinsurance.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReInsuranceRisksDetails {

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("RiskAddress")
	private String riskAddress;
	
	@JsonProperty("RiskCategory")
	private String riskCategory;
	
	@JsonProperty("RiskRefNo")
	private String riskRefNo;
	
	@JsonProperty("CoverFacPrec")
	private String coverFac;
	
	@JsonProperty("PMLPercentage")
	private String pmlPercentage;
	
	@JsonProperty("RecType")
	private String recType;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("InceptionDate")
	private Date inceptionDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EndStartDate")
	private Date endStartDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EndEndDate")
	private Date endEndDate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("PremiaStatus")
	private String premiaStatus;
	
	@JsonProperty("PremiaResponse")
	private String premiaResponse;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdateDate")
	private Date updateDate;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("SectionCode")
	private String sectionCode;
	
	@JsonProperty("EndtTypeName")
	private String endtTypeName;
	
	@JsonProperty("endtTypeId")
	private String endtTypeId;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("UwDivnId")
	private String uwDivnId;
	
	@JsonProperty("UwType")
	private String uwType;
	
	@JsonProperty("UwDeptId")
	private String uwDeptId;
	
	@JsonProperty("UwBusType")
	private String uwBusType;
	
	@JsonProperty("UwRiBasis")
	private String uwRiBasis;
	
	@JsonProperty("UwPremCur")
	private String uwPremCur;
	
	@JsonProperty("RiskSiCur")
	private String riskSiCurr;
	
	@JsonProperty("RiskRecType")
	private String riskRecType;
	
	@JsonProperty("UwLob")
	private String uwLob;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("UwDivnCode")
	private String uwDivnCode;
	
	@JsonProperty("UwDeptCode")
	private String uwDeptCode;
	
	@JsonProperty("ApprovalStatus")
	private String approvalStatus;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("ApprovalDate")
	private Date approvalDate;
	
	@JsonProperty("ApprovalUserId")
	private String approvalUserId;
	
	@JsonProperty("EndtTypeCategoryId")
    private String endtTypeCategoryId ;
    
	@JsonProperty("EndtTypeCategory")
    private String endtTypeCategory ;
    
	@JsonProperty("EndtNo")
	private String endtNo;
	
	@JsonProperty("EndtTypeCode")
	private String endtTypeCode;

	@JsonProperty("InsuredCode")
	private String insuredCode;

	@JsonProperty("InsuredName")
	private String insuredName;

	@JsonProperty("AgBrkCode")
	private String agBrkCode;

	@JsonProperty("AgBrkName")
	private String agBrkName;
	
}
