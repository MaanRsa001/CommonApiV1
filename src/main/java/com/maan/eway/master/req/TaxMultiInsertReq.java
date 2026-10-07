package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaxMultiInsertReq {

		@JsonProperty("TaxId")
		private String     taxId ;

	   @JsonProperty("TaxName")
	    private String     taxName ;
	    
	    @JsonProperty("TaxDesc")
	    private String     taxDesc ;
	    
	    

	    @JsonProperty("CalcType")
	    private String     calcType ;

	    
	    @JsonProperty("Value")
	    private String    value ;

	    @JsonProperty("Status")
	    private String     status ; 
}
