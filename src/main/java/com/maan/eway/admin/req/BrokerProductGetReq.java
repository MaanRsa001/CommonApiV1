package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerProductGetReq {

	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonFormat(pattern ="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart ;
}
