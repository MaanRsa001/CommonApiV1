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
@AllArgsConstructor
@NoArgsConstructor
public class MotorBodySaveReq {

	@JsonProperty("BodyId")
	private String bodyId;
	@JsonProperty("SectionId")
	private String sectionId;
	@JsonProperty("BodyNameEn")
	private String bodyNameEn;
	@JsonProperty("InsuranceId")
	private String companyId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("SeatingCapacity")
	private String seatingCapacity	;
	@JsonProperty("Tonnage")
	private String tonnage	;
	@JsonProperty("Cylinders")
	private String cylinders	;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("BodyType")
	private String bodyType;
}                                      
  
   