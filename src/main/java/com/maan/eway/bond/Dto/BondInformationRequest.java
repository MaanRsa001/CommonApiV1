package com.maan.eway.bond.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BondInformationRequest {

	//@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	//@JsonProperty("QuoteNo")
	private String quoteNo;
	
	//@JsonProperty("ProductId")
	private String productId;
	
	//@JsonProperty("CompanyId")
	private String companyId;
	
	//@JsonProperty("LocationId")
	private String locationId;
	
	//@JsonProperty("PrincipalName")
	private String principalName;
	
	//@JsonProperty("BusinessRegnNo")
	private String businessRegnNo;
	
	//@JsonProperty("TypeOfBusiness")
	private String typeOfBusiness;
	
	//@JsonProperty("Industry")
	private String industry;
	
	//@JsonProperty("YearsBusiness")
	private String yearsBusiness;
	
	//@JsonProperty("ProjectName")
	private String projectName;
	
	//@JsonProperty("ProjectDesc")
	private String projectDesc;
	
	//@JsonProperty("ProjectAddress")
	private String projectAddress;
	
	//@JsonProperty("ProjectLocation")
	private String projectLocation;
	
	//("NameOfObligee")
	private String nameOfObligee;
	
	//@JsonProperty("ProjectOwnerType")
	private String projectOwnerType;
	
	//@JsonProperty("ProjectOwnerTypeDesc")
	private String projectOwnerTypeDesc;
	
	//@JsonProperty("ContractNo")
	private String contractNo;
	
	//@JsonProperty("ProjectStartDate")
	private String projectStartDate;
	
	//@JsonProperty("ProjectEndDate")
	private String projectEndDate;
	
	//@JsonProperty("TotalContractValue")
	private String totalContractValue;
	
	//@JsonProperty("LiquidateDamagesClause")
	private String liquidateDamagesClause;
	
	//@JsonProperty("LiquidateDamagesClauDesc")
	private String liquidateDamagesClauDesc;
	
	//@JsonProperty("AdvancePaymentClause")
	private String advancePaymentClause;
	
	//@JsonProperty("AdvancePaymentClauseDesc")
	private String advancePaymentClauseDesc;
	
	//@JsonProperty("AdvancePaymentValue")
	private String advancePaymentValue;
	
	//@JsonProperty("BondDuration")
	private String bondDuration;
	
	//@JsonProperty("FormOfBond")
	private String formOfBond;
	
	//@JsonProperty("FormOfBondDesc")
	private String formOfBondDesc;
	
	//("WordingProvider")
	private String wordingProvider;
	
	//@JsonProperty("WordingProviderDesc")
	private String wordingProviderDesc;
	
	//@JsonProperty("Jurdication")
	private String jurdication;
	
	//@JsonProperty("GoverningLaw")
	private String governingLaw;
	
	//@JsonProperty("CollateralFixedDepost")
	private String collateralFixedDepost;
	
	//@JsonProperty("CollateralProperty")
	private String collateralProperty;
	
	//@JsonProperty("CollateralCorporate")
	private String collateralCorporate;
	
	//("CollateralPersonal")
	private String collateralPersonal;
	
	//@JsonProperty("EndorsementYn")
	private String endorsementYn;
	
	//@JsonProperty("EndorsementDate")
	private String endorsementDate;
	
	//("EndorsementEffectiveDate")
	private String endorsementEffectiveDate;
	
	//("EndorsementRemarks")
	private String endorsementRemarks;
	
	//("EndorsementType")
	private String endorsementType;
	
	//@JsonProperty("EndorsementTypeDesc")
	private String endorsementTypeDesc;
	
	//("EndtCategoryDesc")
	private String endtCategoryDesc;
	
	//@JsonProperty("EndtCount")
	private String endtCount;
	
	//@JsonProperty("EndtPrevPolicyNo")
	private String endtPrevPolicyNo;
	
	//@JsonProperty("EndtPrevQuoteNo")
	private String endtPrevQuoteNo;
	
	//@JsonProperty("EndtStatus")
	private String endtStatus;
	
	//@JsonProperty("IsFinanceEndt")
	private String isFinanceEndt;
	
	//@JsonProperty("OrginalPolicyNo")
	private String orginalPolicyNo;
	
	//@JsonProperty("LoginId")
	private String loginId;
	
	//@JsonProperty("Status")
	private String status;
	
	//@JsonProperty("Remarks")
	private String remarks;

	
}

