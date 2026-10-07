package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MakeModelChangeStatusReq {

	@JsonProperty("MakeId")
	private String makeId;

	@JsonProperty("ModelId")
	private String modelId;
	
	@JsonProperty("BodyId")
	private String bodyId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("Status")
	private String status;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
