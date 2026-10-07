package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubCoverUpdatedReq {

	    @JsonProperty("CoverId")
	    private String coverId;
	    
	    @JsonProperty("ProductId")
		private String productId;
		
		@JsonProperty("SectionId")
		private String sectionId;
		
		@JsonProperty("InsuranceId")
		private String companyId;
		
	    @JsonProperty("SubCoverId")
	    private String subCoverId;
	    
	    @JsonProperty("SubCoverName")
	    private String subCoverName;
	    

		@JsonFormat(pattern = "dd/MM/yyyy")
		@JsonProperty("EffectiveDateStart")
		private Date effectiveDateStart;
		
		@JsonFormat(pattern = "dd/MM/yyyy")
		@JsonProperty("EffectiveDateEnd")
		private Date effectiveDateEnd;
		
	    @JsonProperty("SubCoverDesc")
	    private String subCoverDesc;
	    
	    @JsonProperty("CoreAppCode")
	    private String coreAppCode;
	    
	    @JsonProperty("RegulatoryCode")
	    private String regulatoryCode;
	    
	    @JsonProperty("CreatedBy")
	    private String createdBy;
	    
	    @JsonProperty("Status")
	    private String status;

	    @JsonProperty("Remarks")
	    private String remarks;
	    
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
		
		@JsonProperty("DependentCoverYn")
	    private String dependentCoverYn;
	    
		@JsonProperty("DependentCoverId")
	    private String dependentCoverId;
}
