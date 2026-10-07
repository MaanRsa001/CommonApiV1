package com.maan.eway.integration.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InsertCreditLimitDetailReq implements Serializable {

	private static final long serialVersionUID = 1L;


	@JsonProperty("Requestreferenceno")
	private String requestreferenceno;

	@JsonProperty("ServiceId")
	private String serviceId;

	@JsonProperty("CompanyCode")
	private String companyCode;

	@JsonProperty("Divisioncode")
	private String divisionCode;

	@JsonProperty("CustomerCode")
	private String customerCode;

	@JsonProperty("Department")
	private String department;

	@JsonProperty("BusinessType")
	private String businessType;

	@JsonProperty("CurrencyCode")
	private String currencyCode;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("AsOnDate")
	private Date asOnDate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RequestTime")
	private Date requestTime;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ResponseTime")
	private Date responseTime;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("PWsResponseType")
	private String pWsResponseType;

	@JsonProperty("PWsError")
	private String pWsError;

	@JsonProperty("PWsResponseErrorDesc")
	private String pWsResponseErrorDesc;

	@JsonProperty("Product")
	private String product;
	
	@JsonProperty("CrdEndNoIdx")
    private String     crdEndNoIdx ;
	
	 @JsonProperty(  "RECORD_TYPE_ID")
		private String 	recordTypeId;

}
