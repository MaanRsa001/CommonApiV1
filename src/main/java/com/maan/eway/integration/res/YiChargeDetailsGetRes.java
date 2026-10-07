package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiChargeDetailsGetRes {
	@JsonProperty("ChargeCode")
	  private String chargeCode ;
	  
	  @JsonProperty("RequestReferenceNo")
	    private String requestreferenceno;
	  	
	  @JsonProperty("ServiceId")
	    private String     serviceId ;
	  	
	  @JsonProperty("QuotationPolicyNo")
	    private String     quotationPolicyNo ;

	  @JsonProperty("ServiceAction")
	    private String     serviceAction ;

	  @JsonProperty("ChgId")
	    private String chgId ;
	    
	  @JsonProperty("IndexNo")
	    private String     indexNo ;

	  @JsonProperty("ChargeRate")
	    private String     chargeRate ;

	  @JsonProperty("RateModifiedYn")
	    private String     rateModifiedYn ;
	  
	  @JsonProperty("ChargeAmount")
	    private String     chargeAmount ;

	  @JsonProperty("ChargeModifiedYn")
	    private String     chargeModifiedYn ;

	  @JsonProperty("ProdCode")
	    private String     prodCode ;

	  @JsonProperty("PWsResponseType")
	    private String     pWsResponseType ;

	  @JsonProperty("PWsError")
	    private String     pWsError ;

	  @JsonFormat(pattern = "dd/MM/yyyy")
	  @JsonProperty("RequestTime")
	    private Date       requestTime ;

	  @JsonFormat(pattern = "dd/MM/yyyy")
	  @JsonProperty("ResponseTime")
	    private Date       responseTime ;

	  @JsonProperty("Status")
	    private String     status ;

}
