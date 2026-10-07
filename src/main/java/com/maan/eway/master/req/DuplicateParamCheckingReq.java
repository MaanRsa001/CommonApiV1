package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DuplicateParamCheckingReq {

	@JsonProperty("Param1" )
    private Double param1 ;
    
    @JsonProperty("Param2" )
    private Double param2 ;
    
    @JsonProperty("Param3" )
    private Double param3 ;
    
    @JsonProperty("Param4" )
    private Double param4 ;
    
    @JsonProperty("Param5" )
    private Double param5 ;
    
    @JsonProperty("Param6" )
    private Double param6 ;
    
    @JsonProperty("Param7" )
    private Double param7 ;
    
    @JsonProperty("Param8" )
    private Double param8 ;
    
    @JsonProperty("Param9" )
    private String param9 ;
    
    @JsonProperty("Param10" )
    private String param10 ;
    
    @JsonProperty("Param11" )
    private String param11 ;
    
    @JsonProperty("Param12" )
    private String param12 ;
    
    @JsonProperty("RowNo" )
    private Long rowNo ;
   
}
