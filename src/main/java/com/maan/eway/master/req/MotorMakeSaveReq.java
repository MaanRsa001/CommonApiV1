package com.maan.eway.master.req;



import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorMakeSaveReq {

	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("MakeNameEn")
	private String makeNameEn;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
		
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("ColorDesc")
	private String colorDesc;
		
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;


	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
