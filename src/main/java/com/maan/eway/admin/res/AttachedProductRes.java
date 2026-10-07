package com.maan.eway.admin.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachedProductRes {

	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("StartLimit")
	private String startLimit ;
	
	@JsonProperty("EndLimit")
	private String endLimit ;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd ;
	
	
}
