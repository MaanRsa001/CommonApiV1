package com.maan.eway.embedded.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InalipaDetailsRes1 {

	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("IntimatedDate")
	private String intimatedDate;
	
	@JsonProperty("ClaimType")
	private String claimType;
	
}
