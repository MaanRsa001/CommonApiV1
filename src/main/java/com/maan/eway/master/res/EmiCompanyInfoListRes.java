package com.maan.eway.master.res;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiCompanyInfoListRes implements Serializable {

    private static final long serialVersionUID = 1L;	

	@JsonProperty("PremiumStart")
	private String premiumStart;
	
	@JsonProperty("PremiumEnd")
	private String premiumEnd; 

	@JsonProperty("Interest")
	private String interest;
	
	@JsonProperty("Advance")
	private String advance;
	


	

	
}
