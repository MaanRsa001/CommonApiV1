package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoverDetailsRes {

	@JsonProperty("CoverName")
	private String coverName;

	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("PremiumAfterDiscount")
	private String premiumAfterDiscount;
	
	@JsonProperty("PremiumIncludedTax")
	private String premiumIncludedTax;
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
}
