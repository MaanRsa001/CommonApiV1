package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCompanyProductsGetRes {

	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("OldProductName")
	private String oldProductName ;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	

	@JsonProperty("Status")
	private String status;
	
	
}
