package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotCommDiscountDetailGetRes {
		
		@JsonProperty("commercialDiscount")
	 	private String     commercialDiscount ;

		@JsonProperty("requestreferenceno")
	    private String     requestreferenceno ;

		@JsonProperty("serviceId")
	    private String     serviceId ;

		@JsonProperty("quotationPolicyNo")
	    private String     quotationPolicyNo ;

		@JsonProperty("secCode")
	    private String     secCode ;

		@JsonProperty("serviceAction")
	    private String     serviceAction ;

		@JsonProperty("indexNo")
	    private String     indexNo ;

		@JsonProperty("l1s1Id")
	    private String     l1s1Id ;

		@JsonProperty("l2s1Id")
	    private String     l2s1Id ;

		@JsonProperty("discountPercentage")
	    private String     discountPercentage ;

		@JsonProperty("discountUserId")
	    private String     discountUserId ;

		@JsonProperty("prodCode")
	    private String     prodCode ;

		@JsonProperty("pWsResponseType")
	    private String     pWsResponseType ;

		@JsonProperty("pWsError")
	    private String     pWsError ;

		@JsonProperty("requestTime")
		@JsonFormat(pattern = "dd/MM/YYYY")
	    private Date       requestTime ;

		@JsonProperty("responseTime")
		@JsonFormat(pattern = "dd/MM/YYYY")
	    private Date       responseTime ;

		@JsonProperty("status")
	    private String     status ;

}
