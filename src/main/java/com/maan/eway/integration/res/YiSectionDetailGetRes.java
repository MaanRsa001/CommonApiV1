package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiSectionDetailGetRes {
	private static final long serialVersionUID = 1L;
	 
	@JsonProperty("RequestReferenceNo")
    private String     requestreferenceno ;

	@JsonProperty("serviceId")
    private String     serviceId ;

	@JsonProperty("serviceAction")
    private String     serviceAction ;

	@JsonProperty("quotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("srNo")
    private String     srNo ;

	@JsonProperty("sectionCode")
    private String     sectionCode ;

	@JsonProperty("iterationNo")
    private String     iterationNo ;

	@JsonProperty("requestTime")
	@JsonFormat(pattern = "dd/MM/YYYY")
    private Date       requestTime ;

	@JsonProperty("responseTime")
	@JsonFormat(pattern = "dd/MM/YYYY")
    private Date       responseTime ;

	@JsonProperty("status")
    private String     status ;

	@JsonProperty("pWsResponseType")
    private String     pWsResponseType ;

	@JsonProperty("pWsError")
    private String     pWsError ;

	@JsonProperty("productCode")
    private String     productCode ;

	@JsonProperty("schemeDesc")
    private String     schemeDesc ;
}
