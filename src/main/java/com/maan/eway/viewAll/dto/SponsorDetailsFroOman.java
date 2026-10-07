package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SponsorDetailsFroOman {
	
	@JsonProperty("SponsorName")
	private String sponsorName;
	
	@JsonProperty("SponsorAdress")
	private String sponsorAdress;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
}
