package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateViewReq {

	@JsonProperty("ProductId")
    private String productId    ;
	
	@JsonProperty("FactorTypeId")
    private String factorTypeId  ;
	
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("Limit")
    private String     limit ;
	
	@JsonProperty("Offset")
    private String     offset ;
}
