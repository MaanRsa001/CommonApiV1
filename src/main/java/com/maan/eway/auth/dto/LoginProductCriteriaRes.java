package com.maan.eway.auth.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginProductCriteriaRes {

	@JsonProperty("ProductId")
	private Integer productId ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	
	@JsonProperty("OldProductName")
	private String oldProductName ;
	
	@JsonProperty("SumInsuredStart")
	private BigDecimal sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private BigDecimal sumInsuredEnd;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks ;
	
	@JsonProperty("CompanyName")
	private String companyName ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date EffectiveDateEnd ;
	
	
	
}
