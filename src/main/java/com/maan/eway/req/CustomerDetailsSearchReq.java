package com.maan.eway.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerDetailsSearchReq implements Serializable {

    private static final long serialVersionUID = 1L;

	
	@JsonProperty("InsuranceId")
    private String     insuranceId ;
	
	@JsonProperty("GstNo")
    private String     gstNo;
    
    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;
    

}
