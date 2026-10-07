package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalStatusListRes {
	
	@JsonProperty("StatusCode")
    private String statusCode;
	
	@JsonProperty("StatusDescription")
    private String statusDescription;
	
	@JsonProperty("Count")
    private String count;
	
	@JsonProperty("DisplayOrder")
    private String displayOrder;
}
