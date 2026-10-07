package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubCoverChangeStatusReq {

	 @JsonProperty("CoverId")
	 private String coverId;
	
	 @JsonProperty("SubCoverId")
	 private String subCoverId;
	 
	 @JsonProperty("Status")
	 private String status;
	 
	 @JsonFormat(pattern ="dd/MM/yyyy")
	 @JsonProperty("EffectiveDateStart")
	 private Date effectiveDateStart;
}
