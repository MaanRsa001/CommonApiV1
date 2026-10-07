package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MotorPrivateAccessoriesDetails {

	@JsonProperty("ItemNo")
	private String itemNo;
	
	@JsonProperty("ItemDesc")
	private String itemDesc;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("SerialNoDesc")
	private String serialNoDesc;
	
}
