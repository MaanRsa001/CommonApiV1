package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitSaveReq {

	@JsonProperty("BenefitId")
	private String benefitId;
	
	@JsonProperty("BenefitDescription")
	private String benefitDescription;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("SubCoverId")
	private String subCoverId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("LongDesc")
	private String longDesc;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("Value")
	private String value;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("DisplayOrder")
	private String displayOrder;

//	@JsonProperty("ImageFile")
//	private Object imageFile;


}
