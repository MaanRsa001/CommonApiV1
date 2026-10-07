package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreditLimitDetailGetRes {
		
		@JsonProperty("RequestReferenceNo")
	    private String     requestreferenceno ;

		@JsonProperty("ServiceId")
	    private String     serviceId ;

		@JsonProperty("CompanyCode")
	    private String   companyCode ;

		@JsonProperty("DivisionCode")
	    private String   divisionCode ;
	    
		@JsonProperty("CustomerCode")
	    private String   customerCode ;
	    
		@JsonProperty("Department")
	    private String   department;
	    
		@JsonProperty("BusinessType")
	    private String   businessType;
	    
		@JsonProperty("CurrencyCode")
	    private String   currencyCode;
	    
		@JsonProperty("AsOnDate")
	    private Date     asOnDate;

		@JsonProperty("RequestTime")
		@JsonFormat(pattern = "dd/MM/YYYY")
	    private Date       requestTime ;

		@JsonProperty("ResponseTime")
		@JsonFormat(pattern = "dd/MM/YYYY")
	    private Date       responseTime ;

		@JsonProperty("Status")
	    private String     status ;
	    
		@JsonProperty("PWsResponseType")
	    private String     pWsResponseType ;

		@JsonProperty("PWsError")
	    private String     pWsError ;

		@JsonProperty("PWsResponseErrorDesc")
	    private String     pWsResponseErrorDesc ;

		@JsonProperty("Product")
	    private String   product;


}
