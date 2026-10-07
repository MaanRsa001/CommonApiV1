package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentOnlineRes {
	
	@JsonProperty("RequestTime")
    private Date       requestTime  ;
	@JsonProperty("ResponseTime")
    private Date       responseTime ;
	@JsonProperty("ResponseMessage")
    private String     responseMessage ;
	@JsonProperty("ResponseStatus")
    private String     responseStatus ;
	@JsonProperty("AuthTransRefNo")
    private String     authTransRefNo ;
	@JsonProperty("AuthAmount")
    private String     authAmount   ;
	@JsonProperty("AuthResponse")
    private String     authResponse ;
	@JsonProperty("Channel")
    private String     channel ;
	@JsonProperty("Reference")
    private String     reference ;
	@JsonProperty("Msisdn")
    private String     msisdn ;
   

}
