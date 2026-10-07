package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorParamsInsert {
	
	@JsonProperty("SNo" )
	private String sno;

	@JsonProperty("Param1" )
    private String param1 ;
    
    @JsonProperty("Param2" )
    private String param2 ;
    
    @JsonProperty("Param3" )
    private String param3 ;
    
    @JsonProperty("Param4" )
    private String param4 ;
    
    @JsonProperty("Param5" )
    private String param5 ;
    
    @JsonProperty("Param6" )
    private String param6 ;
    
    @JsonProperty("Param7" )
    private String param7 ;
    
    @JsonProperty("Param8" )
    private String param8 ;
    
    @JsonProperty("Param9" )
    private String param9 ;
    
    @JsonProperty("Param10" )
    private String param10 ;
    
    @JsonProperty("Param11" )
    private String param11 ;
    
    @JsonProperty("Param12" )
    private String param12 ;
    
    @JsonProperty("Rate" )
    private String rate ;
    
    @JsonProperty("MinimumPremium" )
    private String minimumPremium ;
    
    @JsonProperty("CalcType" )
    private String calType ;
    
    @JsonProperty("Status" )
    private String status ;
  
    @JsonProperty("RegulatoryCode" )
    private String regulatoryCode;
    
}
