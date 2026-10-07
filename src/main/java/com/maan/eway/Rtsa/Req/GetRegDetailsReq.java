package com.maan.eway.Rtsa.Req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRegDetailsReq {

	@JsonProperty("RegNo")
	private String regNo;
	
	@JsonProperty("RequestToken")
	private String requestToken;
	
	
}
