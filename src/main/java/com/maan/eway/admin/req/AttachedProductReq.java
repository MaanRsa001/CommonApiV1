package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachedProductReq {

	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate ;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("CommissionVatYn")
	private String commissionVatYn;
	
	@JsonProperty("CheckerYn")
	private String checkerYn;
	
	@JsonProperty("MakerYn")
	private String makerYn;
	
	@JsonProperty("CustConfirmYn")
	private String custConfirmYn;
	
	@JsonProperty("PaymentYn")
	private String paymentYn;
	
	@JsonProperty("CommissionPercent")
	private String commissionPercent;
	
	
	
}
