package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorTypeGetReq {


	@JsonProperty("ProductId")
    private String productId    ;
	
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	
	@JsonProperty("FactorTypeId")
    private String     factorTypeId;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
