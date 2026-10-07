package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("OccupationId")
	private String occupationId;

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("OccupationName")
	private String occupationName;

	@JsonProperty("OccupationNameAr")
	private String occupationNameAr;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;	
	
/*	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd; */
	
	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ProductId")
	private String productId;

	
	@JsonProperty("CategoryId")
	private String categoryId;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;


}
