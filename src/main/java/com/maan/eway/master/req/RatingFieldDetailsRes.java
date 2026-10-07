package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RatingFieldDetailsRes {

	@JsonProperty("RatingFiledId")
    private String ratingFieldId     ;
	
	@JsonProperty("RangeYn")
    private String     rangeYn      ;
	
	@JsonProperty("ColumnsId")
    private String     columnsId ;
	
	@JsonProperty("Status")
    private String     status ;
	
	@JsonProperty("FromColumnName")
    private String     fromColumnName ;
	
	@JsonProperty("FromDisplayName")
    private String     fromDisplayName ;
	
	@JsonProperty("ToColumnName")
    private String     toColumnName ;
	
	@JsonProperty("ToDisplayName")
    private String     toDisplayName ;
	
	@JsonProperty("DiscreteColumnName")
    private String discreteColumnName;
	
	@JsonProperty("DiscreteDisplayName")
    private String discreteDisplayName;
}
