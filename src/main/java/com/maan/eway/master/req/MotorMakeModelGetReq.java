package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorMakeModelGetReq {

	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("ModelId")
	private String modelId;
	
	@JsonProperty("BodyId")
	private String bodyId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
