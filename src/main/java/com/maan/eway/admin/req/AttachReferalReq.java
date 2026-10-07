package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachReferalReq {


	@JsonProperty("ReferalId")
	private String referalId ;
	
	@JsonProperty("ReferalName")
	private String referalName ;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart ;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd ;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonProperty("Remarks")
	private String remarks ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate ;
}
