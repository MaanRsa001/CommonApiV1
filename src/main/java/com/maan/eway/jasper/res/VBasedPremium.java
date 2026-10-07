package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class VBasedPremium {
	
	@JsonProperty("RegNo")
	private String regNo;
	
	@JsonProperty("CoverPremium")
	private  List<PremiumDetailsWithOuttax> list; 
	
	

}
