package com.maan.eway.res.referal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterReferal {
	
	@JsonProperty("isReferral")
	private Boolean isreferral;
	
	@JsonProperty("ReferralDesc")
	private String referralDesc;
	
	@JsonProperty("ApiInfo")
	private String apiInfo;

}
