package com.maan.eway.master.res;

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
public class MotorBodyTypeGetRes {

	@JsonProperty("BodyId")
	private Integer bodyId;
	
	@JsonProperty("BodyNameEn")
	private String bodyNameEn;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("AmendId")
	private Integer amendId;

	@JsonProperty("SeatingCapacity")
	private String seatingCapacity;

	@JsonProperty("Tonnage")
	private Integer tonnage;

	@JsonProperty("Cylinders")
	private String cylinders;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty( "UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("BodyType")
	private String bodyType;


}
