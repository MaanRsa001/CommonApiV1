package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionCoverUpdateReq {
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("CoverDesc")
	private String coverDesc;
	
	@JsonProperty("Status")
	private String status;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("Remarks")
	private String remarks; 
	
	@JsonProperty("CreatedBy")
	private String createdBy; 
	
	@JsonProperty("ToolTip")
	private String toolTip;  

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("SubCoverYn")
	private String subCoverYn;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	
	// Rating Master Insert 
	@JsonProperty("CoverageType")
	private String coverageType;
	
	@JsonProperty("CoverageLimit")
	private String coverageLimit;
	
	@JsonProperty("Excess")
	private String excess;
	
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("BaseRate")
	private String baseRate;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("MinimumPremium")
	private String minimumPremium;
	
	@JsonProperty("FactorTypeId")
	private String factorTypeId;
	
	@JsonProperty("GridDetails")
	private List<OfsGridSaveReq> gridDetails;

	@JsonProperty("IsTaxExcempted")
	private String isTaxExcempted;
	
	@JsonProperty("TaxAmount")
	private String taxAmount;
	
	@JsonProperty("TaxCode")
	private String taxCode;
	
	@JsonProperty("TaxExcemptionReference")
	private String taxExcemptionReference;
	
	@JsonProperty("TaxExcemptionType")
	private String taxExcemptionType;
	

	@JsonProperty("DiscountCoverId")
	private String discountCoverId;
	
	@JsonProperty("DependentCoverYn")
    private String dependentCoverYn;
    
	@JsonProperty("DependentCoverId")
    private String dependentCoverId;
	
	@JsonProperty("IsSelectedYn")
    private String isSelectedYn;
}
