package com.maan.eway.integration.req;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsertYiVatDetailReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno;

	@JsonProperty("ServiceId")
	private String serviceId;

	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;

	@JsonProperty("ServiceAction")
	private String serviceAction;

	@JsonProperty("IndexNo")
	private String indexNo;

	@JsonProperty("VatSrNo")
	private Double vatSrNo;

	@JsonProperty("VatId")
	private BigDecimal vatId;

	@JsonProperty("VatApplyOn")
	private Double vatApplyOn;

	@JsonProperty("VatCode")
	private String vatCode;

	@JsonProperty("VatPerc")
	private Double vatPerc;

	@JsonProperty("VatAmount")
	private Double vatAmount;

	@JsonProperty("VatModifiedYn")
	private String vatModifiedYn;

	@JsonProperty("ProdCode")
	private String prodCode;

	@JsonProperty("PWsResponseType")
	private String pWsResponseType;

	@JsonProperty("PWsError")
	private String pWsError;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RequestTime")
	private Date requestTime;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ResponseTime")
	private Date responseTime;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("VatEndNoIdx")
    private String     vatEndNoIdx ;
	
	 @JsonProperty(  "RECORD_TYPE_ID")
		private String 	recordTypeId;

}
