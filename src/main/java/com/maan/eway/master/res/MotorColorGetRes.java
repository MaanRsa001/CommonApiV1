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
public class MotorColorGetRes {

	@JsonProperty("ColorId")
	private Integer colorId;
	@JsonProperty("ColorCode")
	private String colorCode;
	@JsonProperty("ColorDesc")
	private String colorDesc;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	
	@JsonProperty("AmendId")
	private Integer amendId;
	
	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;

}
