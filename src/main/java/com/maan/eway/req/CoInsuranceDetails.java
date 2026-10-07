package com.maan.eway.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoInsuranceDetails {
	
	@JsonProperty("Amendid")
	private int amendid ;
	
	@JsonProperty("Quoteno")
	private String quoteno ;
	
	@JsonProperty("Requestreferenceno")
	private String requestreferenceno ;
	
	
	@JsonProperty("Productid")
	private int productid ;
	
	@JsonProperty("CoInsurerList")
	private List<CoInsuranceInfoReq> coinsreq;

}
