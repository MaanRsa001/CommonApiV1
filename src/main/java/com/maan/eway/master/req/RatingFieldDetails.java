package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RatingFieldDetails {

	@JsonProperty("RatingFiledId")
    private String ratingFieldId     ;
	
	@JsonProperty("RangeYn")
    private String     rangeYn      ;
	
	@JsonProperty("ColumnsId")
    private String     columnsId ;
	
	@JsonProperty("Status")
    private String     status ;
	
	@JsonProperty("FromDisplayName")
    private String     fromDisplayName ;
	
	@JsonProperty("ToDisplayName")
    private String     toDisplayName ;
	
	@JsonProperty("DiscreteDisplayName")
    private String discreteDisplayName;
	
}
