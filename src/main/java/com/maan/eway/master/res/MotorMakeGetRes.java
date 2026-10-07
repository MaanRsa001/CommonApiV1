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
public class MotorMakeGetRes {

	@JsonProperty("MakeId")
	private Integer makeId;
	
	@JsonProperty("MakeNameEn")
	private String makeNameEn;
	
	@JsonProperty("ColorDesc")
	private String colorDesc;
	
	
	@JsonProperty("AmendId")
	private Integer amendId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	  
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;

}
