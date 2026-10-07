package com.maan.eway.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class CoInsuranceInfoReq {
	
	
	@JsonProperty("Sno")
	private int sno ;
	
	@JsonProperty("Insurancecompanyid")
	private int insurancecompanyid ;
	
	@JsonProperty("Insurancecompanyname")
	private String insurancecompanyname ;

	@JsonProperty("Sharedpercentage")
	private BigDecimal  sharedpercentage ;

	
	@JsonProperty("Leaderparticipant")
	private String leaderparticipant ;
	

 }
