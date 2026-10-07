package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubCoverMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("SubCoverId")
    private String subCoverId; 
	
	@JsonProperty("CoverId")
    private String coverId; 
	    
	 @JsonFormat(pattern ="dd/MM/yyyy")
	 @JsonProperty("EffectiveDateStart")
	 private Date effectiveDateStart;
}
