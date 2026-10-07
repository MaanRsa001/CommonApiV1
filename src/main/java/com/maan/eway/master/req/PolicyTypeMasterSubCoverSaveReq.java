package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.master.res.PolicyTypeSubCoverMasterGetRes;

import lombok.Data;

@Data
public class PolicyTypeMasterSubCoverSaveReq {

	@JsonProperty("PlanTypeId")
    private String planTypeId ;
	
	@JsonProperty("PlanTypeDesc")
    private String planTypeDesc ;
	
	@JsonProperty("PolicyTypeId")
    private String PolicyTypeId ;
	
	@JsonProperty("PolicyTypeDesc")
    private String PolicyTypeDesc ;
	
	@JsonProperty("CoverId")
    private String    coverId ;
	
	@JsonProperty("CoverDesc")
    private String    coverDesc;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CoverStatus")
	private String coverStatus;
	 
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	 
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;
	
	@JsonProperty("SubCoverDetails")
    private List<PolicyTypeSubCoverMasterGetRes> travelSubCover;

	
	
	
}
